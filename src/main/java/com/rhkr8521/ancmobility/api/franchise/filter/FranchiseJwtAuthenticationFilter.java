package com.rhkr8521.ancmobility.api.franchise.filter;

import com.rhkr8521.ancmobility.api.franchise.repository.FranchiseRepository;
import com.rhkr8521.ancmobility.api.member.jwt.service.JwtService;
import com.rhkr8521.ancmobility.common.SecurityFranchiseMember;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class FranchiseJwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final FranchiseRepository franchiseRepository;

    /** 필터를 적용할 URI 들 */
    private static final List<String> FILTER_URLS = List.of(
            "/api/v1/franchise/check",
            "/api/v1/franchise/settlement"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        String token = header.substring(7);
        // ① 서명 + 만료 검증, ② “FRANCHISE” 타입 검증
        if (!jwtService.isTokenValid(token) || !jwtService.isFranchiseToken(token)) {
            chain.doFilter(req, res);
            return;
        }

        jwtService.extractFranchiseId(token)
                .map(Long::valueOf)
                .flatMap(franchiseRepository::findById)
                .ifPresent(fr -> {
                    SecurityFranchiseMember principal = SecurityFranchiseMember.builder()
                            .id(fr.getId())
                            .name(fr.getName())
                            .phoneNumber(fr.getPhoneNumber())
                            .carNumber(fr.getCarNumber())
                            .build();

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    principal,
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_FRANCHISE"))
                            );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                });

        chain.doFilter(req, res);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();

        // 멤버 API나 swagger 등 공통 제외가 필요하면 여기에 추가
//        if (uri.startsWith("/api/v1/member") || uri.startsWith("/swagger-ui")) {
//            return true;
//        }

        // FILTER_URLS 중 하나라도 startsWith 하면 필터 실행 대상 → shouldNotFilter = false
        boolean isTarget = FILTER_URLS.stream()
                .anyMatch(uri::startsWith);

        return !isTarget;
    }
}
