package com.rhkr8521.ancmobility.common.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)

public enum ErrorStatus {
    /**
     * 400 BAD_REQUEST
     */
    VALIDATION_REQUEST_MISSING_EXCEPTION(HttpStatus.BAD_REQUEST, "요청 값이 입력되지 않았습니다."),
    ALREADY_REGISTER_USERID_EXCPETION(HttpStatus.BAD_REQUEST,"이미 등록된 사용자ID 입니다."),
    ALREADY_REGISTER_EMAIL_EXCPETION(HttpStatus.BAD_REQUEST,"이미 등록된 이메일 입니다."),
    ALREADY_REGISTER_NICKNAME_EXCPETION(HttpStatus.BAD_REQUEST,"이미 등록된 닉네임 입니다."),
    WRONG_PASSWORD_EXCEPTION(HttpStatus.BAD_REQUEST,"아이디 또는 비밀번호가 잘못되었습니다."),
    MISSING_REFRESH_TOKEN_EXCEPTION(HttpStatus.BAD_REQUEST,"리프레시 토큰이 입력되지 않았습니다."),
    UNAUTHORIZED_REFRESH_TOKEN_EXCEPTION(HttpStatus.BAD_REQUEST,"유효하지 않은 리프레시 토큰 입니다."),
    NEED_ADMIN_ROLE_EXCEPTION(HttpStatus.BAD_REQUEST,"관리자 권한이 필요합니다."),
    ALREADY_CREATE_TERM_EXCEPTION(HttpStatus.BAD_REQUEST,"이미 해당 타입의 약관이 등록되었습니다."),

    /**
     * 401 UNAUTHORIZED
     */
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"인증되지 않은 사용자입니다."),

    /**
     * 404 NOT_FOUND
     */

    NOT_LOGIN_EXCEPTION(HttpStatus.NOT_FOUND,"로그인이 필요합니다."),
    USER_NOTFOUND_EXCEPTION(HttpStatus.NOT_FOUND,"해당 사용자를 찾을 수 없습니다."),
    TERM_TYPE_NOTFOUND_EXCEPTION(HttpStatus.NOT_FOUND,"해당 약관 타입이 존재하지 않습니다."),

    /**
     * 500 SERVER_ERROR
     */
    FAIL_UPLOAD_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR,"파일 업로드 실패하였습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getStatusCode() {
        return this.httpStatus.value();
    }
}