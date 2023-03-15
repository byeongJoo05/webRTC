package com.dreamkakao.webrtc.api.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dreamkakao.webrtc.api.request.MakeRoomReq;
import com.dreamkakao.webrtc.api.service.RoomService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dreamkakao.webrtc.db.entity.Room;
import com.dreamkakao.webrtc.api.request.FindRoomReq;
import com.dreamkakao.webrtc.api.request.LeaveRoomReq;
import com.dreamkakao.webrtc.api.response.RoomRes;
import com.dreamkakao.webrtc.util.RandomNumberUtil;

import io.openvidu.java.client.Connection;
import io.openvidu.java.client.ConnectionProperties;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Session;
import io.openvidu.java.client.SessionProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@Log4j2
public class RoomController {
	private final RoomService roomService;
	private final int limit = 6;    // 방 인원 제한

	// OpenVidu 객체 SDK
	private OpenVidu openVidu;

	// room 관리
	private Map<String, Integer> roomSessions = new ConcurrentHashMap<>();

	// OpenVidu 서버 관련 변수
	private String OPENVIDU_URL;
	private String SECRET;

	// RoomController에 접근할 때마다 OpenVidu 서버 관련 변수를 얻어옴
	@Autowired
	public RoomController(RoomService roomService, @Value("${openvidu.secret}") String secret,
		@Value("${openvidu.url}") String openviduUrl) {
		this.roomService = roomService;
		this.SECRET = secret;
		this.OPENVIDU_URL = openviduUrl;
		this.openVidu = new OpenVidu(OPENVIDU_URL, SECRET);
	}

	// 세션 초기화
	@PostMapping("/api/sessions")
	public ResponseEntity<String> initializeSession(@RequestBody(required = false) Map<String, Object> params)
		throws OpenViduJavaClientException, OpenViduHttpException {
		SessionProperties properties = SessionProperties.fromJson(params).build();
		Session session = openVidu.createSession(properties);

		return new ResponseEntity<>(session.getSessionId(), HttpStatus.OK);
	}

	// 커넥션 생성
	@PostMapping("/api/sessions/{sessionId}/connections")
	public ResponseEntity<String> createConnection(@PathVariable("sessionId") String sessionId, @RequestBody(required = false) Map<String, Object> params)
		throws OpenViduJavaClientException, OpenViduHttpException {
		Session session = openVidu.getActiveSession(sessionId);

		if (session == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		ConnectionProperties properties = ConnectionProperties.fromJson(params).build();
		Connection connection = session.createConnection(properties);

		return new ResponseEntity<>(connection.getToken(), HttpStatus.OK);
	}

	// room 생성
	@PostMapping("/api/rooms")
	public ResponseEntity<RoomRes> makeRoom(@RequestBody MakeRoomReq makeRoomReq) throws
		OpenViduJavaClientException,
		OpenViduHttpException {
		// room 번호 난수 생성
		String roomId = RandomNumberUtil.getRandomNumber();
		log.warn(roomId);
		// room 관리 map에 저장
		this.roomSessions.put(roomId, 1);

		// DB 저장
		roomService.makeRoom(roomId);

		return ResponseEntity.ok(roomService.getRoomRes(roomId));
	}

	// room 검색
	@PostMapping("/api/rooms/search")
	public ResponseEntity<RoomRes> findRoom(@RequestBody FindRoomReq findRoomReq) throws
		OpenViduJavaClientException,
		OpenViduHttpException {
		// 검색할 방이 존재하는지 확인
		Room room = roomService.findRoom(findRoomReq);
		String roomId = room.getRoomId();

		// 검색하는 방이 존재하지 않을 경우
		if (this.roomSessions.get(roomId) == null) {
			System.out.println("방 존재하지 않음");
			log.warn("RoomNotFoundException");
			log.warn(roomId);
		}

		// 인원초과일 경우
		if (this.roomSessions.get(roomId) >= limit) {
			System.out.println("인원 초과");
			log.warn("RoomStatusIsNotAvailableException");
			log.warn(room.getStatus());
		}

		// room 관리 map에 저장
		this.roomSessions.put(roomId, this.roomSessions.get(roomId) + 1);

		return ResponseEntity.ok(roomService.getRoomRes(roomId));
	}

	// room 퇴장
	@PutMapping("/api/rooms")
	public ResponseEntity leaveRoom(@RequestBody LeaveRoomReq leaveRoomReq) {
		String roomId = leaveRoomReq.getRoomId();

		// 나가려는 방이 없다면
		if (this.roomSessions.get(roomId) == null) {
			System.out.println("예외!! 방이 존재하지않음");
			log.warn("RoomNotFoundException");
			log.warn(roomId);
		}

		int cnt = this.roomSessions.get(roomId);    // 방에 남아있는 인원수

		// 마지막 게스트 라면
		if (cnt == 1) {
			// room 삭제
			// room 관리 map에서 삭제
			this.roomSessions.remove(roomId);

			// DB에서 OFF로 상태 업데이트
			roomService.updateStatus(roomId);
		} else {    // 마지막 게스트가 아니라면
			// room 관리 map에서 인원수 갱신
			this.roomSessions.put(roomId, cnt - 1);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
