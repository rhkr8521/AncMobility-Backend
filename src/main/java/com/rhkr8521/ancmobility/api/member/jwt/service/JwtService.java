package com.rhkr8521.ancmobility.api.member.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
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

    @Value("${jwt.franchise.expiration}")
    private Long franchiseTokenExpirationPeriod;

    private final MemberRepository memberRepository;

    // Access Token 생성
    public String createAccessToken(Long memberId) {
        Date now = new Date();
        return JWT.create()
                .withSubject(String.valueOf(memberId))
                .withClaim("tokenType", "MEMBER")
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

    // 가맹점 전용 Access Token 생성
    public String createFranchiseToken(Long franchiseId) {
        Date now = new Date();
        return JWT.create()
                .withSubject(String.valueOf(franchiseId))
                .withClaim("tokenType", "FRANCHISE")
                .withExpiresAt(new Date(now.getTime() + franchiseTokenExpirationPeriod))
                // 필요하다면 .withClaim("type","FRANCHISE") 같이 토큰 구분용 클레임 추가
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

    // 가맹점 토큰에서 franchiseId 추출
    public Optional<String> extractFranchiseId(String token) {
        try {
            String sub = JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(token)
                    .getSubject();
            return Optional.ofNullable(sub);
        } catch (Exception e) {
            log.error("가맹점 토큰 검증 실패: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public boolean isFranchiseToken(String token) {
        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            return "FRANCHISE".equals(jwt.getClaim("tokenType").asString());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isMemberToken(String token) {
        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(token);
            return "MEMBER".equals(jwt.getClaim("tokenType").asString());
        } catch (Exception e) {
            log.error("Member 토큰 타입 검증 실패: {}", e.getMessage());
            return false;
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