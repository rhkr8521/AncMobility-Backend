package com.rhkr8521.ancmobility.common.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public enum SuccessStatus {

    /**
     * 200
     */
    SEND_REGISTER_SUCCESS(HttpStatus.OK,"회원가입 성공"),
    SEND_LOGIN_SUCCESS(HttpStatus.OK,"로그인 성공"),
    SEND_REISSUE_TOKEN_SUCCESS(HttpStatus.OK,"토큰 재발급 성공"),
    GET_USERINFO_SUCCESS(HttpStatus.OK,"사용자 정보 조회 성공"),
    UPDATE_TERM_SUCCESS(HttpStatus.OK,"약관 수정 성공"),
    GET_TERM_SUCCESS(HttpStatus.OK,"약관 조회 성공"),
    SEND_NOTICE_SUCCESS(HttpStatus.OK,"공지사항 조회 성공"),
    UPDATE_NOTICE_SUCCESS(HttpStatus.OK,"공지사항 수정 성공"),
    DELETE_NOTICE_SUCCESS(HttpStatus.OK,"공지사항 삭제 성공"),
    SEND_FAQ_SUCCESS(HttpStatus.OK,"FAQ 조회 성공"),
    UPDATE_FAQ_SUCCESS(HttpStatus.OK,"FAQ 수정 성공"),
    DELETE_FAQ_SUCCESS(HttpStatus.OK,"FAQ 삭제 성공"),

    /**
     * 201
     */
    CREATE_NOTICE_SUCCESS(HttpStatus.CREATED, "공지사항 등록 성공"),
    SAVE_TERM_SUCCESS(HttpStatus.CREATED,"약관 등록 성공"),
    CREATE_FAQ_SUCCESS(HttpStatus.CREATED, "FAQ 등록 성공"),

    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getStatusCode() {
        return this.httpStatus.value();
    }
}