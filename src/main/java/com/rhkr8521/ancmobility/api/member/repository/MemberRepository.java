package com.rhkr8521.ancmobility.api.member.repository;

import com.rhkr8521.ancmobility.api.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(String userId);
    Optional<Member> findByRefreshToken(String refreshToken);
    Optional<Member> findByEmail(String email);
}