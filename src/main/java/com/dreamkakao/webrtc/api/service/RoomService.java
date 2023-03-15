package com.dreamkakao.webrtc.api.service;

import com.dreamkakao.webrtc.db.entity.Room;
import com.dreamkakao.webrtc.db.entity.User;
import com.dreamkakao.webrtc.db.repository.RoomRepository;
import com.dreamkakao.webrtc.db.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

	private final RoomRepository roomRepository;
	private final UserRepository userRepository;

	@Autowired
	public RoomService(RoomRepository roomRepository, UserRepository userRepository) {
		this.roomRepository = roomRepository;
		this.userRepository = userRepository;
	}

	// 방 만들기
	@Transactional
	public void makeRoom(String roomId, final makeRoomReq makeRoomReq) {
		Room room = Room.builder()
				.roomId(roomId)
				.isPublic(makeRoomReq.getPassword() == "" ? true : false)
				.password(makeRoomReq.getPassword())
				.status("ON")
				.build();
		roomRepository.save(room);
	}

	@Transactional
	public Room findRoom(final FindRoomReq findRoomReq) {
		Room room = roomRepository.findByRoomId(findRoomReq.getRoomId());
		if (room == null) {
			throw new RoomNotFoundException(findRoomReq.getRoomId());
		}
		if (!room.getPassword().equals(findRoomReq.getPassword())) {
			throw new RoomPasswordNotMatchException(room.getRoomId());
		}
		if (room.getStatus().equals("OFF")) {
			throw new RoomNotFoundException(room.getStatus());
		}
		if (room.getStatus().equals("GAME")) {
			throw new RoomStatusIsNotAvailableException(room.getStatus());
		}
		if (!room.getStatus().equals("ON")) {
			throw new RoomStatusIsNotAvailableException(room.getStatus());
		}
		return roomRepository.findByRoomIdAndAndPasswordAndStatus(findRoomReq.getRoomId(), findRoomReq.getPassword(), "ON").orElse(null);
	}

	@Transactional
	public void updateStatus(String roomId) {
		Room updateRoom = roomRepository.findById(roomId).orElse(null);
		if (updateRoom == null) {
			throw new RoomNotFoundException(roomId);
		}
		updateRoom.setStatus("OFF");
		roomRepository.save(updateRoom);
	}

	@Transactional(readOnly = true)
	public RoomRes getRoomRes(String roomId, Integer gameType) {
		RoomRes roomRes = new RoomRes();
		User user = userRepository.findOneByEmail(SecurityUtil.getCurrentEmail().orElse("")).orElse(null);
		roomRes.setRoomId(roomId);
		roomRes.setGameType(gameType);
		roomRes.setNickname(user.getNickname());
		return roomRes;
	}
}
