package com.likelion.ganzithon.auth.repository;

import com.likelion.ganzithon.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

//    로그인 -> 닉네임으로 사용자 조회
    Optional<User> findByNickname(String nickname);
}


