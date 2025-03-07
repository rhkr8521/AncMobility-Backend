package com.rhkr8521.ancmobility.api.term.controller;

import com.rhkr8521.ancmobility.api.term.dto.TermRequestDTO;
import com.rhkr8521.ancmobility.api.term.dto.TermResponseDTO;
import com.rhkr8521.ancmobility.api.term.entity.TermType;
import com.rhkr8521.ancmobility.api.term.service.TermService;
import com.rhkr8521.ancmobility.common.SecurityMember;
import com.rhkr8521.ancmobility.common.response.ApiResponse;
import com.rhkr8521.ancmobility.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Term", description = "이용약관 및 개인정보처리방침 관련 API")
@RestController
@RequestMapping("/api/v1/term")
@RequiredArgsConstructor
public class TermController {

    private final TermService termService;

    @Operation(summary = "이용약관/개인정보처리방침 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createTerm(@RequestBody TermRequestDTO dto,
                                                        @AuthenticationPrincipal SecurityMember securityMember) {

        termService.createTerm(dto, securityMember.getRole());
        return ApiResponse.success_only(SuccessStatus.SAVE_TERM_SUCCESS);
    }

    @Operation(summary = "이용약관/개인정보처리방침 수정")
    @PutMapping
    public ResponseEntity<ApiResponse<Void>> updateTerm(@RequestBody TermRequestDTO dto,
                                                        @AuthenticationPrincipal SecurityMember securityMember) {

        termService.updateTerm(dto, securityMember.getRole());
        return ApiResponse.success_only(SuccessStatus.UPDATE_TERM_SUCCESS);
    }

    @Operation(summary = "이용약관/개인정보처리방침 조회")
    @GetMapping("/{termType}")
    public ResponseEntity<ApiResponse<TermResponseDTO>> getTerm(@PathVariable TermType termType) {

        TermResponseDTO responseDTO = termService.getTermByType(termType);
        return ApiResponse.success(SuccessStatus.GET_TERM_SUCCESS, responseDTO);
    }
}
