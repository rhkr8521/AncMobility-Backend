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
    UPDATE_COMPANYINFO_SUCCESS(HttpStatus.OK,"회사 정보 수정 성공"),
    GET_COMPANYINFO_SUCCESS(HttpStatus.OK,"회사 정보 조회 성공"),
    UPDATE_SERVICEINFO_SUCCESS(HttpStatus.OK,"서비스 정보 수정 성공"),
    GET_SERVICEINFO_SUCCESS(HttpStatus.OK,"서비스 정보 조회 성공"),
    UPDATE_HOMEINFO_SUCCESS(HttpStatus.OK,"홈 화면 정보 수정 성공"),
    GET_HOMEINFO_SUCCESS(HttpStatus.OK,"홈 화면 정보 조회 성공"),
    SEND_NEWS_SUCCESS(HttpStatus.OK,"뉴스 조회 성공"),
    UPDATE_NEWS_SUCCESS(HttpStatus.OK,"뉴스 수정 성공"),
    DELETE_NEWS_SUCCESS(HttpStatus.OK,"뉴스 삭제 성공"),
    GET_ALLIANCE_SUCCESS(HttpStatus.OK,"제휴 정보 조회 성공"),
    UPDATE_ALLIANCE_SUCCESS(HttpStatus.OK,"제휴 정보 수정 성공"),
    DELETE_ALLIANCE_SUCCESS(HttpStatus.OK,"제휴 정보 삭제 성공"),
    SEND_SMS_VERIFICATION_CODE_SUCCESS(HttpStatus.OK,"SMS 인증코드 발송 성공"),
    SEND_VERIFY_SMS_CODE_SUCCESS(HttpStatus.OK,"SMS 코드 인증 성공"),
    SEND_FRANCHISE_LIST_SUCCESS(HttpStatus.OK, "가맹점 리스트 조회 성공"),
    UPDATE_FRANCHISE_LIST_SUCCESS(HttpStatus.OK, "가맹점 수정 성공"),
    DELETE_FRANCHISE_LIST_SUCCESS(HttpStatus.OK, "가맹점 삭제 성공"),
    SEND_SETTLEMENT_LIST_SUCCESS(HttpStatus.OK,"가맹점 매출 조회 성공"),
    UPDATE_FRANCHISE_SUCCESS(HttpStatus.OK,"가맹점 수정 성공"),
    DELETE_FRANCHISE_SUCCESS(HttpStatus.OK,"가맹점 삭제 성공"),
    DELETE_SETTLEMENT_SUCCESS(HttpStatus.OK, "정산내역 삭제 성공"),
    SEND_FRANCHISE_SEARCH_SUCCESS(HttpStatus.OK,"가맹점 검색 성공"),
    SEND_SETTLEMENT_SEARCH_SUCCESS(HttpStatus.OK,"매출내역 검색 성공"),
    UPDATE_CONTACT_SUCCESS(HttpStatus.OK,"문의처 정보 수정 성공"),
    GET_CONTACT_SUCCESS(HttpStatus.OK,"문의처 정보 조회 성공"),

    /**
     * 201
     */
    CREATE_NOTICE_SUCCESS(HttpStatus.CREATED, "공지사항 등록 성공"),
    SAVE_TERM_SUCCESS(HttpStatus.CREATED,"약관 등록 성공"),
    CREATE_FAQ_SUCCESS(HttpStatus.CREATED, "FAQ 등록 성공"),
    SAVE_COMPANYINFO_SUCCESS(HttpStatus.CREATED,"회사 정보 등록 성공"),
    SAVE_SERVICEINFO_SUCCESS(HttpStatus.CREATED,"서비스 정보 등록 성공"),
    SAVE_HOMEINFO_SUCCESS(HttpStatus.CREATED,"홈 화면 정보 등록 성공"),
    CREATE_NEWS_SUCCESS(HttpStatus.CREATED,"뉴스 등록 성공"),
    CREATE_ALLIANCE_SUCCESS(HttpStatus.CREATED,"제휴 정보 등록 성공"),
    SEND_SETTLEMENT_UPLOAD_SUCCESS(HttpStatus.CREATED,"정산 내역 등록 성공"),
    CREATE_FRANCHISE_SUCCESS(HttpStatus.CREATED,"가맹점 등록 성공"),
    SAVE_CONTACT_SUCCESS(HttpStatus.CREATED,"문의처 정보 등록 성공"),

    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getStatusCode() {
        return this.httpStatus.value();
    }
}