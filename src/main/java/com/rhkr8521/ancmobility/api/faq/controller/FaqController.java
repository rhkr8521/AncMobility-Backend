package com.rhkr8521.ancmobility.api.faq.controller;

import com.rhkr8521.ancmobility.api.faq.dto.FaqCreateDTO;
import com.rhkr8521.ancmobility.api.faq.dto.FaqPageResponseDTO;
import com.rhkr8521.ancmobility.api.faq.service.FaqService;
import com.rhkr8521.ancmobility.common.SecurityMember;
import com.rhkr8521.ancmobility.common.response.ApiResponse;
import com.rhkr8521.ancmobility.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "FaQ", description = "FaQ 관련 API 입니다.")
@RestController
@RequestMapping("/api/v1/faq")
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;

    @Operation(
            summary = "FaQ 목록 조회"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<FaqPageResponseDTO>> getFaqs(
            @RequestParam(defaultValue = "0") int page,   // 기본값 0
            @RequestParam(defaultValue = "10") int size    // 기본값 10
    ) {
        FaqPageResponseDTO response = faqService.getFaqs(page, size);
        return ApiResponse.success(SuccessStatus.SEND_FAQ_SUCCESS, response);
    }

    @Operation(
            summary = "FaQ 등록"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createFaq(
            @RequestBody FaqCreateDTO faqCreateDTO,
            @AuthenticationPrincipal SecurityMember securityMember
    ) {

        faqService.createFaq(faqCreateDTO,securityMember.getId());
        return ApiResponse.success_only(SuccessStatus.CREATE_FAQ_SUCCESS);
    }

    @Operation(
            summary = "FaQ 수정"
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateFaq(
            @PathVariable Long id,
            @RequestBody FaqCreateDTO faqCreateDTO,
            @AuthenticationPrincipal SecurityMember securityMember
    ) {

        faqService.updateFaq(id, faqCreateDTO, securityMember.getId());
        return ApiResponse.success_only(SuccessStatus.UPDATE_FAQ_SUCCESS);
    }

    @Operation(
            summary = "FaQ 삭제"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFaq(@PathVariable Long id,
                                                          @AuthenticationPrincipal SecurityMember securityMember) {

        faqService.deleteFaq(id, securityMember.getId());
        return ApiResponse.success_only(SuccessStatus.DELETE_FAQ_SUCCESS);
    }
}
