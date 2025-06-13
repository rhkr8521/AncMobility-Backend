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
    ALREADY_CREATE_COMPANYINFO_EXCEPTION(HttpStatus.BAD_REQUEST,"이미 해당 타입의 회사 정보가 등록되었습니다."),
    ALREADY_CREATE_SERVICEINFO_EXCEPTION(HttpStatus.BAD_REQUEST,"이미 해당 타입의 서비스 정보가 등록되었습니다."),
    ALREADY_CREATE_HOMEINFO_EXCEPTION(HttpStatus.BAD_REQUEST,"이미 홈 정보가 등록되어 있습니다."),
    ALREADY_CREATE_CONTRACT_EXCEPTION(HttpStatus.BAD_REQUEST,"이미 문의처 정보가 등록되어 있습니다."),
    VALIDATION_PHONE_FORMAT_EXCEPTION(HttpStatus.BAD_REQUEST, "휴대폰 번호 형식이 올바르지 않습니다."),
    MISSING_FRANCHISE_INFO_EXCEPTION(HttpStatus.BAD_REQUEST,"미등록 된 가맹점 또는 정산데이터가 없습니다."),
    WRONG_SMS_VERIFICATION_CODE_EXCEPTION(HttpStatus.BAD_REQUEST,"SMS 인증코드가 올바르지 않습니다."),
    VALIDATION_DUPLICATE_FRANCHISE_EXCEPTION(HttpStatus.BAD_REQUEST,"이미 등록된 가맹점 입니다."),

    /**
     * 401 UNAUTHORIZED
     */
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"인증되지 않은 사용자입니다."),
    UNAUTHORIZED_SMS_VERIFICATION_CODE_EXCEPTION(HttpStatus.UNAUTHORIZED,"SMS 인증코드가 만료되었습니다, 재인증 해주세요."),

    /**
     * 404 NOT_FOUND
     */

    NOT_LOGIN_EXCEPTION(HttpStatus.NOT_FOUND,"로그인이 필요합니다."),
    USER_NOTFOUND_EXCEPTION(HttpStatus.NOT_FOUND,"해당 사용자를 찾을 수 없습니다."),
    TERM_TYPE_NOTFOUND_EXCEPTION(HttpStatus.NOT_FOUND,"해당 약관 타입이 존재하지 않습니다."),
    COMPANYINFO_TYPE_NOTFOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 회사 정보 타입이 존재하지 않습니다."),
    SERVICEINFO_TYPE_NOTFOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 서비스 정보 타입이 존재하지 않습니다."),
    HOMEINFO_NOTFOUND_EXCEPTION(HttpStatus.NOT_FOUND, "홈 정보가 존재하지 않습니다."),
    CONTACT_NOTFOUND_EXCEPTION(HttpStatus.NOT_FOUND,"문의처 정보가 존재하지 않습니다."),
    NOTICE_NOTFOUND_EXCEPTION(HttpStatus.NOT_FOUND,"공지사항을 찾을 수 없습니다."),
    FAQ_NOTFOUND_EXCEPTION(HttpStatus.NOT_FOUND,"FAQ를 찾을 수 없습니다."),
    NEWS_NOTFOUND_EXCEPTION(HttpStatus.NOT_FOUND,"뉴스를 찾을 수 없습니다."),
    ALLIANCE_NOTFOUND_EXCEPTION(HttpStatus.NOT_FOUND,"제휴 정보를 찾을 수 없습니다."),
    NOT_FOUND_FRANCHISE_EXCEPTION(HttpStatus.NOT_FOUND,"올바르지 않은 가맹점 입니다."),

    /**
     * 500 SERVER_ERROR
     */
    FAIL_IMAGE_UPLOAD_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR,"이미지 저장 중 오류가 발생하였습니다."),
    SMS_SEND_FAILED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR,"SMS 전송에 실패하였습니다."),
    FAIL_UPLOAD_SETTLEMENT_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR,"정산 내역 등록 중 오류가 발생하였습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getStatusCode() {
        return this.httpStatus.value();
    }
}