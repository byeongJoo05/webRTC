package com.dreamkakao.webrtc.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MakeRoomReq {

	@NotNull(message = "gameType may not be empty")
	private Integer gameType;

	@NotNull(message = "password may not be null")
	@Size(max = 50)
	private String password;
}
