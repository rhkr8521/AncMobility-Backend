package com.rhkr8521.ancmobility.api.franchise.controller;

import com.rhkr8521.ancmobility.api.franchise.dto.*;
import com.rhkr8521.ancmobility.api.franchise.service.FranchiseService;
import com.rhkr8521.ancmobility.api.franchise.service.SmsService;
import com.rhkr8521.ancmobility.api.news.dto.NewsPageResponseDTO;
import com.rhkr8521.ancmobility.common.SecurityFranchiseMember;
import com.rhkr8521.ancmobility.common.SecurityMember;
import com.rhkr8521.ancmobility.common.exception.BadRequestException;
import com.rhkr8521.ancmobility.common.response.ApiResponse;
import com.rhkr8521.ancmobility.common.response.ErrorStatus;
import com.rhkr8521.ancmobility.common.response.SuccessStatus;
import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Franchise", description = "가맹점 관련 API 입니다.")
@RestController
@RequestMapping("/api/v1/franchise")
@RequiredArgsConstructor
public class FranchiseController {

    private final FranchiseService franchiseService;
    private final SmsService smsService;

    @Operation(
            summary = "SMS 인증코드 발송 API",
            description = "휴대폰으로 인증코드를 발송합니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "phoneNumber : 전화번호 (예시 : 01012345678)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "SMS 인증코드 발송 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "휴대폰 번호 형식이 올바르지 않습니다."),
    })
    @PostMapping("/verify-phone")
    public ResponseEntity<ApiResponse<Void>> sendVerificationSms(@RequestBody PhoneAuthRequestDTO phoneAuthRequestDTO) {

        String phoneNumber = phoneAuthRequestDTO.getPhoneNumber();
        LocalDateTime requestedAt = LocalDateTime.now();

        if (StringUtils.isBlank(phoneNumber) || !phoneNumber.matches("\\d{10,11}")) {
            throw new BadRequestException(ErrorStatus.VALIDATION_PHONE_FORMAT_EXCEPTION.getMessage());
        }

        smsService.sendVerificationSms(phoneAuthRequestDTO, requestedAt);
        return ApiResponse.success_only(SuccessStatus.SEND_SMS_VERIFICATION_CODE_SUCCESS);
    }

    @Operation(
            summary = "SMS 코드 인증 API",
            description = "발송된 SMS 인증 코드를 검증합니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "code : 문자로 발송된 인증코드"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "SMS 코드 인증 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "SMS 인증코드가 올바르지 않습니다."),
    })
    @PostMapping("/verification-phone-code")
    public ResponseEntity<ApiResponse<String>> verifyPhoneCode(@RequestBody PhoneAuthVerifyDTO phoneAuthVerifyDTO) {

        LocalDateTime requestedAt = LocalDateTime.now();
        String token = smsService.verifyCodeAndIssueToken(phoneAuthVerifyDTO.getCode(), requestedAt);
        return ApiResponse.success(SuccessStatus.SEND_VERIFY_SMS_CODE_SUCCESS, token);
    }

    @Operation(
            summary = "관리자용: 정산내역 업로드 API"
    )
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<Void>> uploadSettlements(
            @Parameter(description = "정산 엑셀 파일", required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal SecurityMember securityMember
    ) {
        franchiseService.importFromExcel(file, securityMember.getId());
        return ApiResponse.success_only(SuccessStatus.SEND_SETTLEMENT_UPLOAD_SUCCESS);
    }

    @Operation(
            summary = "관리자용: 가맹점 목록 조회"
    )
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<FranchiseListResponseDTO>> getFranchiseList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal SecurityMember securityMember
    ) {
        FranchiseListResponseDTO response = franchiseService.getFranchiseList(page, size, securityMember.getId());
        return ApiResponse.success(SuccessStatus.SEND_FRANCHISE_LIST_SUCCESS, response);
    }

    @Operation(summary = "사용자용: 날짜별 정산 내역 조회",
            description = "`date=YYYY-MM-DD` 로 조회")
    @GetMapping("/settlement")
    public ResponseEntity<ApiResponse<SettlementListResponseDTO<SettlementResponseDTO>>> getSettlement(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal SecurityFranchiseMember securityFranchiseMember
    ) {
        SettlementListResponseDTO<SettlementResponseDTO> settlementListResponseDTO =
                franchiseService.getSettlementByDate(date, securityFranchiseMember.getId());
        return ApiResponse.success(SuccessStatus.SEND_SETTLEMENT_LIST_SUCCESS, settlementListResponseDTO);
    }

    @Operation(summary = "관리자용: 날짜별 정산 내역 페이징 조회",
            description = "`date=YYYY-MM-DD`, `page`, `size` 파라미터 사용")
    @GetMapping("/settlement/admin")
    public ResponseEntity<ApiResponse<SettlementListResponseDTO<SettlementAdminResponseDTO>>> getSettlementAdmin(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal SecurityMember securityMember
    ) {
        SettlementListResponseDTO<SettlementAdminResponseDTO> settlementListResponseDTO =
                franchiseService.getSettlementAdminByDate(date, page, size, securityMember.getId());
        return ApiResponse.success(SuccessStatus.SEND_SETTLEMENT_LIST_SUCCESS, settlementListResponseDTO);
    }

    @Operation(summary = "관리자용: 가맹점 등록")
    @PostMapping("/admin")
    public ResponseEntity<ApiResponse<Void>> createFranchise(
            @RequestBody FranchiseCreateRequestDTO franchiseCreateRequestDTO,
            @AuthenticationPrincipal SecurityMember securityMember
    ) {
        franchiseService.createFranchise(franchiseCreateRequestDTO, securityMember.getId());
        return ApiResponse.success_only(SuccessStatus.CREATE_FRANCHISE_SUCCESS);
    }

    @Operation(summary = "관리자용: 가맹점 수정")
    @PatchMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<Void>> updateFranchise(
            @PathVariable Long id,
            @RequestBody FranchiseCreateRequestDTO franchiseCreateRequestDTO,
            @AuthenticationPrincipal SecurityMember securityMember
    ) {
        franchiseService.updateFranchise(id, franchiseCreateRequestDTO, securityMember.getId());
        return ApiResponse.success_only(SuccessStatus.UPDATE_FRANCHISE_SUCCESS);
    }

    @Operation(summary = "관리자용: 가맹점 삭제")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFranchise(@PathVariable Long id, @AuthenticationPrincipal SecurityMember securityMember) {
        franchiseService.deleteFranchise(id, securityMember.getId());
        return ApiResponse.success_only(SuccessStatus.DELETE_FRANCHISE_SUCCESS);
    }

    @Operation(summary = "관리자용: 정산 데이터 삭제")
    @DeleteMapping("/settlement/admin/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSettlement(@PathVariable Long id, @AuthenticationPrincipal SecurityMember securityMember) {
        franchiseService.deleteSettlement(id, securityMember.getId());
        return ApiResponse.success_only(SuccessStatus.DELETE_SETTLEMENT_SUCCESS);
    }
}
