package com.dreamkakao.webrtc.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MakeRoomReq {

	@NotNull(message = "name may not be empty")
	private String name;	// 사용자 이름

	@NotNull(message = "password may not be null")
	@Size(max = 50)
	private String password;	// 비밀번호
}
