package com.dreamkakao.webrtc.api.service;

import com.dreamkakao.webrtc.api.request.FindRoomReq;
import com.dreamkakao.webrtc.api.request.MakeRoomReq;
import com.dreamkakao.webrtc.api.response.RoomRes;
import com.dreamkakao.webrtc.db.entity.Room;
import com.dreamkakao.webrtc.db.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class RoomService {

	private final RoomRepository roomRepository;

	@Autowired
	public RoomService(RoomRepository roomRepository) {
		this.roomRepository = roomRepository;
	}

	// 방 만들기
	@Transactional
	public void makeRoom(String roomId) {
		Room room = Room.builder()
				.roomId(roomId)
				.status("ON")
				.build();
		roomRepository.save(room);
	}

	@Transactional
	public Room findRoom(final FindRoomReq findRoomReq) {
		Room room = roomRepository.findByRoomId(findRoomReq.getRoomId());
		if (room == null) {
			log.warn("RoomNotFoundException");
			log.warn(findRoomReq.getRoomId());
		}
		if (room.getStatus().equals("OFF")) {
			log.warn("RoomNotFoundException");
			log.warn(room.getStatus());
		}
		if (room.getStatus().equals("GAME")) {
			log.warn("RoomStatusIsNotAvailableException");
			log.warn(room.getStatus());
		}
		if (!room.getStatus().equals("ON")) {
			log.warn("RoomStatusIsNotAvailableException");
			log.warn(room.getStatus());
		}
		return roomRepository.findByRoomIdAndStatus(findRoomReq.getRoomId(), "ON").orElse(null);
	}

	@Transactional
	public void updateStatus(String roomId) {
		Room updateRoom = roomRepository.findById(roomId).orElse(null);
		if (updateRoom == null) {
			log.warn("RoomNotFoundException");
			log.warn(roomId);
		}
		updateRoom.setStatus("OFF");
		roomRepository.save(updateRoom);
	}

	@Transactional
	public RoomRes getRoomRes(String roomId) {
		RoomRes roomRes = new RoomRes();
		roomRes.setRoomId(roomId);
		return roomRes;
	}
}
