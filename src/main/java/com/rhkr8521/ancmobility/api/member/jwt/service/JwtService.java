package com.rhkr8521.ancmobility.api.member.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.rhkr8521.ancmobility.api.member.entity.Member;
import com.rhkr8521.ancmobility.api.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtService {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    private final Long duplicationTokenExpirationPeriod= 3600000L; // 1시간. 설마 1시간 동안 이메일 변경하겠어?

    private final MemberRepository memberRepository;

    // Access Token 생성
    public String createAccessToken(Long memberId) {
        Date now = new Date();
        return JWT.create()
                .withSubject(String.valueOf(memberId))
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }

    // Refresh Token 생성
    public String createRefreshToken(Long memberId) {
        Date now = new Date();
        return JWT.create()
                .withSubject(String.valueOf(memberId))
                .withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }

    // Duplication Token 생성
    public String createDuplicationToken(Long id) {
        Date now = new Date();
        return JWT.create()
                .withSubject(String.valueOf(id))
                .withExpiresAt(new Date(now.getTime() + duplicationTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }

    // Access Token과 Refresh Token 발급 및 반환
    public Map<String, String> createAccessAndRefreshToken(Long memberId) {
        String accessToken = createAccessToken(memberId);
        String refreshToken = createRefreshToken(memberId);

        // Refresh Token DB에 업데이트
        updateRefreshToken(memberId, refreshToken);

        log.info("Access Token, Refresh Token 발급 완료");
        log.info("Access Token : {}", accessToken);
        log.info("Refresh Token : {}", refreshToken);

        // Access Token과 Refresh Token을 Map으로 반환
        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );
    }

    @Transactional
    public void updateRefreshToken(Long memberId, String refreshToken) {
        memberRepository.findById(memberId).ifPresent(member -> {
            Member updatedMember = member.updateRefreshToken(refreshToken);
            memberRepository.save(updatedMember);
        });
    }

    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            return true;
        } catch (TokenExpiredException e) {
            log.error("토큰이 만료되었습니다: {}", e.getMessage());
            return false;
        } catch (SignatureVerificationException e) {
            log.error("토큰 서명 검증 실패: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다: {}", e.getMessage());
            return false;
        }
    }

    public Optional<String> extractMemberId(String accessToken) {
        try {
            Optional<String> sub = Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(accessToken)
                    .getClaim("sub")
                    .asString());


            return sub;
        } catch (Exception e) {
            log.error("액세스 토큰이 유효하지 않습니다.");
            return Optional.empty();
        }
    }

    public Optional<String> extractDuplicationId(String duplicationToken) {
        try {
            Optional<String> sub = Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(duplicationToken)
                    .getClaim("sub")
                    .asString());


            return sub;
        } catch (Exception e) {
            log.error("중복 토큰이 유효하지 않습니다.");
            return Optional.empty();
        }
    }



    public Optional<String> extractEmail(String accessToken) {
        try {
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(accessToken)
                    .getClaim("sub")
                    .asString());
        } catch (Exception e) {
            log.error("액세스 토큰이 유효하지 않습니다.");
            return Optional.empty();
        }
    }
}