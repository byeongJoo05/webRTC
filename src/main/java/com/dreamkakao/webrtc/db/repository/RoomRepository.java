package com.dreamkakao.webrtc.db.repository;

import com.dreamkakao.webrtc.db.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {

	// 방 조회
	Room findByRoomId(String roomId);

	// 방 id, password, status 조회
	Optional<Room> findByRoomIdAndAndPasswordAndStatus(String roomId, String password, String Status);
}
