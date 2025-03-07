package com.rhkr8521.ancmobility.common.advice;

import com.rhkr8521.ancmobility.common.exception.BadRequestException;
import com.rhkr8521.ancmobility.common.exception.BaseException;
import com.rhkr8521.ancmobility.common.exception.NotFoundException;
import com.rhkr8521.ancmobility.common.response.ApiResponse;
import com.rhkr8521.ancmobility.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionAdvice {

    /**
     * BaseException 계열 처리
     * - BadRequestException, NotFoundException을 제외한 예외에 대해서만 슬랙 알림 전송
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse> handleGlobalException(BaseException ex) {
        // BadRequest, NotFound가 아닌 BaseException 예외라면 슬랙 알림
        if (!(ex instanceof BadRequestException) && !(ex instanceof NotFoundException)) {
            //slackNotificationService.sendServerErrorMessage(ex.getMessage());
        }

        return ResponseEntity.status(ex.getStatusCode())
                .body(ApiResponse.fail(ex.getStatusCode(), ex.getResponseMessage()));
    }

    /**
     * 필수 Request Parameter가 누락되었을 경우
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingParameter(MissingServletRequestParameterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(HttpStatus.BAD_REQUEST.value(),
                        ErrorStatus.VALIDATION_REQUEST_MISSING_EXCEPTION.getMessage()));
    }

    /**
     * 잘못된 인자가 전달되었을 경우
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    /**
     * @Valid 검증 실패 시 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        FieldError fieldError = Objects.requireNonNull(e.getFieldError());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(HttpStatus.BAD_REQUEST.value(),
                        String.format("%s. (%s)", fieldError.getDefaultMessage(), fieldError.getField())));
    }

    /**
     * 그 외 모든 알 수 없는 예외 처리
     * - BaseException을 상속받지 않은 RuntimeException, Exception 등에 대한 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleAnyException(Exception e) {
        // 여기서는 BadRequest, NotFound 예외가 아닌 모든 예외이므로 Slack 알림 전송
        //slackNotificationService.sendServerErrorMessage(e.getMessage());

        log.error("[handleAnyException] 알 수 없는 오류 발생", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "알 수 없는 오류 발생"));
    }

}