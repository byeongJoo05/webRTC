package com.dreamkakao.webrtc.db.repository;

import com.dreamkakao.webrtc.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	// 이메일로 찾기
	Optional<User> findOneByEmail(String email);

	// 같은 닉네임을 가진 사람 모두 찾기
	List<User> findAllByNickname(String nickname);

	// 이메일로 지우기
	Integer deleteByEmail(String email);
}
