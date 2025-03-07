package com.rhkr8521.ancmobility.api.member.service;

import com.rhkr8521.ancmobility.api.member.dto.MemberLoginRequestDTO;
import com.rhkr8521.ancmobility.api.member.dto.MemberLoginResponseDTO;
import com.rhkr8521.ancmobility.api.member.dto.MemberRegisterRequestDTO;
import com.rhkr8521.ancmobility.api.member.dto.UserInfoResponseDTO;
import com.rhkr8521.ancmobility.api.member.entity.Member;
import com.rhkr8521.ancmobility.api.member.entity.Role;
import com.rhkr8521.ancmobility.api.member.jwt.service.JwtService;
import com.rhkr8521.ancmobility.api.member.repository.MemberRepository;
import com.rhkr8521.ancmobility.common.exception.BadRequestException;
import com.rhkr8521.ancmobility.common.exception.NotFoundException;
import com.rhkr8521.ancmobility.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // 회원가입
    @Transactional
    public void register(MemberRegisterRequestDTO memberRegisterRequestDTO) {

        // 사용자ID 중복 검증
        if (memberRepository.findByUserId(memberRegisterRequestDTO.getUserId()).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTER_USERID_EXCPETION.getMessage());
        }

        // 이메일 중복 검증
        if (memberRepository.findByEmail(memberRegisterRequestDTO.getEmail()).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTER_EMAIL_EXCPETION.getMessage());
        }

        // 닉네임 중복 검증
        if (memberRepository.findByEmail(memberRegisterRequestDTO.getUsername()).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTER_NICKNAME_EXCPETION.getMessage());
        }

        // Employee 엔티티 생성
        Member member = Member.builder()
                .userId(memberRegisterRequestDTO.getUserId())
                .password(passwordEncoder.encode(memberRegisterRequestDTO.getPassword())) // 비밀번호 암호화
                .nickname(memberRegisterRequestDTO.getUsername())
                .email(memberRegisterRequestDTO.getEmail())
                .role(Role.USER)
                .build();

        memberRepository.save(member);
    }

    // 로그인
    public MemberLoginResponseDTO login(MemberLoginRequestDTO dto) {

        // userId로 회원 검색
        Member member = memberRepository.findByUserId(dto.getUserId())
                .orElseThrow(() -> new BadRequestException(ErrorStatus.WRONG_PASSWORD_EXCEPTION.getMessage()));

        // 비밀번호 검증
        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new BadRequestException(ErrorStatus.WRONG_PASSWORD_EXCEPTION.getMessage());
        }

        // JWT 토큰 생성 (Access, Refresh)
        Map<String, String> tokens = jwtService.createAccessAndRefreshToken(member.getId());

        // DTO를 사용하여 응답 데이터 구성
        return new MemberLoginResponseDTO(
                tokens.get("accessToken"),
                tokens.get("refreshToken"),
                member.getRole().name()
        );
    }

    // 사용자 정보 조회
    @Transactional(readOnly = true)
    public UserInfoResponseDTO getUserInfo(Long userId) {

        // 해당 유저를 찾을 수 없을 경우 예외처리
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        return new UserInfoResponseDTO(member);
    }
}
