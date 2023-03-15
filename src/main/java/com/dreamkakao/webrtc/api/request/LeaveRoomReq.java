package com.dreamkakao.webrtc.api.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveRoomReq {
	@NotEmpty(message = "roomId may not be empty")
	@Size(max = 50)
	private String roomId;
}
