package com.dreamkakao.webrtc.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import io.openvidu.java.client.OpenVidu;

@RestController
public class RoomController {
	// todo roomService 추가

	// OpenVidu 객체 SDK
	private OpenVidu openVidu;

	// room 관리
	private Map<String, Integer> roomSessions = new ConcurrentHashMap<>();

	// OpenVidu 서버 관련 변수
	private String OPENVIDU_URL;
	private String SECRET;

	// RoomController에 접근할 때마다 OpenVidu 서버 관련 변수를 얻어옴
	// TODO
	@Autowired
	public RoomController() {

	}

	// TODO room 생성

	// TODO room 검색

	// TODO room 퇴장
}
