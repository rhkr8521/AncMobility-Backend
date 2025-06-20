package com.rhkr8521.ancmobility.common.config.security;

import com.rhkr8521.ancmobility.api.franchise.filter.FranchiseJwtAuthenticationFilter;
import com.rhkr8521.ancmobility.api.franchise.repository.FranchiseRepository;
import com.rhkr8521.ancmobility.api.member.jwt.service.JwtService;
import com.rhkr8521.ancmobility.common.config.jwt.JwtConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtConfig jwtConfig;
    private final JwtService jwtService;
    private final FranchiseRepository franchiseRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(Arrays.asList(
                            "https://www.ancmobility.co.kr",
                            "https://www.ancmobility.co.kr:81",
                            "http://localhost:5173"
                    ));
                    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH","DELETE", "OPTIONS"));
                    config.setAllowCredentials(true);
                    config.setAllowedHeaders(Arrays.asList("Authorization", "Authorization-Refresh","Content-Type", "X-Requested-With", "Accept", "Origin"));
                    config.setMaxAge(3600L);
                    config.addExposedHeader("Authorization");
                    config.addExposedHeader("Authorization-Refresh");
                    return config;
                }))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api-doc", "/health", "/v3/api-docs/**",
                                "/swagger-resources/**","/swagger-ui/**",
                                "/h2-console/**"
                        ).permitAll() // 스웨거, H2, healthCheck 허가
                        .requestMatchers(
                                "/api/v1/member/register", "/api/v1/notice", "/api/v1/notice/*", "/api/v1/faq", "/api/v1/serviceinfo/*", "/api/v1/home","/api/v1/news", "/api/v1/news/*", "/api/v1/contact",
                                "/api/v1/member/login","/api/v1/member/token-reissue", "/api/v1/term/*", "/api/v1/companyinfo/*", "/api/images/**", "/api/v1/alliance","/api/v1/franchise/verify-phone" , "/api/v1/franchise/verification-phone-code"
                        ).permitAll() // 회원가입, 로그인, 토큰 재발급, 약관, 회사 정보, 서비스 정보, 홈 정보, 뉴스, 제휴 조회, 가맹점 로그인
                        .requestMatchers("/api/v1/franchise/check","/api/v1/franchise/settlement").hasRole("FRANCHISE") // 가맹점 전용 토큰만 사용
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

        http.addFilterBefore(jwtConfig.jwtAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(
                new FranchiseJwtAuthenticationFilter(jwtService, franchiseRepository),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }
}