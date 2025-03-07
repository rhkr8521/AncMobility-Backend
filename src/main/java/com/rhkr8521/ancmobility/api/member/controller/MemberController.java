package com.rhkr8521.ancmobility.api.member.controller;

import com.rhkr8521.ancmobility.api.member.dto.MemberLoginRequestDTO;
import com.rhkr8521.ancmobility.api.member.dto.MemberLoginResponseDTO;
import com.rhkr8521.ancmobility.api.member.dto.UserInfoResponseDTO;
import com.rhkr8521.ancmobility.api.member.jwt.service.JwtService;
import com.rhkr8521.ancmobility.api.member.service.MemberService;
import com.rhkr8521.ancmobility.api.member.dto.MemberRegisterRequestDTO;
import com.rhkr8521.ancmobility.common.SecurityMember;
import com.rhkr8521.ancmobility.common.exception.BadRequestException;
import com.rhkr8521.ancmobility.common.response.ApiResponse;
import com.rhkr8521.ancmobility.common.response.SuccessStatus;
import com.rhkr8521.ancmobility.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Member", description = "Member 관련 API 입니다.")
@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtService jwtService;

    @Operation(
            summary = "회원가입 API"
    )
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody MemberRegisterRequestDTO memberRegisterRequestDTO) {

        memberService.register(memberRegisterRequestDTO);
        return ApiResponse.success_only(SuccessStatus.SEND_REGISTER_SUCCESS);
    }

    @Operation(
            summary = "로그인 API"
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<MemberLoginResponseDTO>> login(@RequestBody MemberLoginRequestDTO memberLoginRequestDTO) {

        MemberLoginResponseDTO responseDTO = memberService.login(memberLoginRequestDTO);
        return ApiResponse.success(SuccessStatus.SEND_LOGIN_SUCCESS, responseDTO);
    }

    @Operation(
            summary = "토큰 재발급 API"
    )
    @GetMapping("/token-reissue")
    public ResponseEntity<ApiResponse<Void>> reissueToken(@RequestHeader(value = "Authorization-Refresh", required = false) String refreshToken) {

        // 리프레시 토큰이 입력되지 않았을 경우 예외 처리
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new BadRequestException(ErrorStatus.MISSING_REFRESH_TOKEN_EXCEPTION.getMessage());
        }

        // "Bearer " 문자열 제거 후 토큰 검증
        String pureRefreshToken = refreshToken.substring(7);
        if (!jwtService.isTokenValid(pureRefreshToken)) {
            // 유효하지 않은 토큰 예외 처리
            throw new BadRequestException(ErrorStatus.UNAUTHORIZED_REFRESH_TOKEN_EXCEPTION.getMessage());
        }

        return ApiResponse.success_only(SuccessStatus.SEND_REISSUE_TOKEN_SUCCESS);
    }

    @Operation(
            summary = "사용자 정보 조회 API"
    )
    @GetMapping("/user-info")
    public ResponseEntity<ApiResponse<UserInfoResponseDTO>> getUserInfo(@AuthenticationPrincipal SecurityMember securityMember) {

        UserInfoResponseDTO userInfo = memberService.getUserInfo(securityMember.getId());
        return ApiResponse.success(SuccessStatus.GET_USERINFO_SUCCESS, userInfo);
    }
}
