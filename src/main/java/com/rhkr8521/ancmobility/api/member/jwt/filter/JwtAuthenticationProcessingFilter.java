package com.rhkr8521.ancmobility.api.member.jwt.filter;

import com.rhkr8521.ancmobility.api.member.entity.Member;
import com.rhkr8521.ancmobility.api.member.jwt.service.JwtService;
import com.rhkr8521.ancmobility.api.member.repository.MemberRepository;
import com.rhkr8521.ancmobility.common.SecurityMember;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    @Value("${jwt.access.header}")
    private String accessTokenHeader;

    @Value("${jwt.refresh.header}")
    private String refreshTokenHeader;

    private static final String TOKEN_REISSUE_URL = "/api/v1/member/token-reissue"; // 토큰 재발급 엔드포인트

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    private static final String[] SWAGGER_URIS = {
            "/swagger-ui",
            "/v3/api-docs",
            "/swagger-ui/index.html"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();

        // Swagger 관련 경로는 필터를 적용하지 않음. 디버깅 불편함.
        for (String uri : SWAGGER_URIS) {
            if (requestURI.startsWith(uri)) {
                return true;
            }
        }

        return false;
    }



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // 리프레시 토큰 재발급 로직은 /token-reissue 엔드포인트에서만 수행
        if (requestURI.equals(TOKEN_REISSUE_URL)) {
            // Refresh Token이 존재하는지 확인
            Optional<String> refreshToken = extractToken(request, refreshTokenHeader)
                    .filter(jwtService::isTokenValid);

            // Refresh Token이 유효하면 Access Token을 재발급하고 인증 정보를 설정
            if (refreshToken.isPresent()) {
                handleRefreshToken(response, refreshToken.get());
            }
            filterChain.doFilter(request, response);
            return;
        }

        // Access Token이 존재하고 유효한지 확인
        Optional<String> accessToken = extractToken(request, accessTokenHeader)
                .filter(jwtService::isTokenValid);

        accessToken.ifPresent(token -> jwtService.extractMemberId(token)
                .ifPresent(id -> memberRepository.findById(Long.valueOf(id))
                        .ifPresent(this::setAuthentication)));

        filterChain.doFilter(request, response);
    }

    // Refresh Token을 처리하여 Access Token 재발급 및 인증 처리
    private void handleRefreshToken(HttpServletResponse response, String refreshToken) {
        memberRepository.findByRefreshToken(refreshToken)
                .ifPresent(user -> {
                    String newAccessToken = jwtService.createAccessToken(user.getId());
                    String newRefreshToken = jwtService.createRefreshToken(user.getId());

                    // Refresh Token 업데이트
                    jwtService.updateRefreshToken(user.getId(), newRefreshToken);

                    // 새로운 Access Token과 Refresh Token을 헤더로 설정
                    response.setHeader(accessTokenHeader, "Bearer " + newAccessToken);
                    response.setHeader(refreshTokenHeader, "Bearer " + newRefreshToken);

                    log.info("Access Token, Refresh Token 재발급 완료");
                    log.info("Access Token : {}", newAccessToken);
                    log.info("Refresh Token : {}", newRefreshToken);
                });
    }

    // 요청 헤더에서 토큰을 추출하는 메서드
    private Optional<String> extractToken(HttpServletRequest request, String headerName) {
        String bearerToken = request.getHeader(headerName);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return Optional.of(bearerToken.substring(7)); // "Bearer " 이후의 토큰 부분만 반환
        }
        return Optional.empty();
    }

    // 인증 정보를 SecurityContext에 설정하는 메서드
    private void setAuthentication(Member member) {
        /*UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().name())
                .build();*/

        SecurityMember securityMember = SecurityMember.builder()
                .id(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .role(member.getRole())
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                securityMember, null, null);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}