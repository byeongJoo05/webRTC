package com.dreamkakao.webrtc.db.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "room")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Room {

	@Id
	@Column(name = "room_id", length = 50)
	private String roomId;

	@Column(name = "status", length = 10)
	private String status;

	@Column(name = "created_at", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Timestamp createdAt;
}
