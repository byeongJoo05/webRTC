package com.dreamkakao.webrtc.api.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindRoomReq {
	@NotEmpty(message = "roomId may not be empty")
	@Size(max = 50)
	private String roomId;
}
