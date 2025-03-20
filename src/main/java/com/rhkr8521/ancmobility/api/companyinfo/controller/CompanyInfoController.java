package com.rhkr8521.ancmobility.api.companyinfo.controller;

import com.rhkr8521.ancmobility.api.companyinfo.dto.CompanyInfoRequestDTO;
import com.rhkr8521.ancmobility.api.companyinfo.dto.CompanyInfoResponseDTO;
import com.rhkr8521.ancmobility.api.companyinfo.entity.CompanyInfoType;
import com.rhkr8521.ancmobility.api.companyinfo.service.CompanyInfoService;
import com.rhkr8521.ancmobility.common.SecurityMember;
import com.rhkr8521.ancmobility.common.response.ApiResponse;
import com.rhkr8521.ancmobility.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "CompanyInfo", description = "회사 정보 관련 API")
@RestController
@RequestMapping("/api/v1/companyinfo")
@RequiredArgsConstructor
public class CompanyInfoController {

    private final CompanyInfoService companyInfoService;

    @Operation(summary = "회사 정보 등록")
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<Void>> createCompanyInfo(
            @ModelAttribute CompanyInfoRequestDTO dto,
            @AuthenticationPrincipal SecurityMember securityMember) {

        companyInfoService.createCompanyInfo(dto, securityMember.getRole());
        return ApiResponse.success_only(SuccessStatus.SAVE_COMPANYINFO_SUCCESS);
    }

    @Operation(summary = "회사 정보 수정")
    @PutMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<Void>> updateCompanyInfo(
            @ModelAttribute CompanyInfoRequestDTO dto,
            @AuthenticationPrincipal SecurityMember securityMember) {

        companyInfoService.updateCompanyInfo(dto, securityMember.getRole());
        return ApiResponse.success_only(SuccessStatus.UPDATE_COMPANYINFO_SUCCESS);
    }

    @Operation(summary = "회사 정보 조회")
    @GetMapping("/{companyInfoType}")
    public ResponseEntity<ApiResponse<CompanyInfoResponseDTO>> getCompanyInfo(
            @PathVariable CompanyInfoType companyInfoType) {

        CompanyInfoResponseDTO responseDTO = companyInfoService.getCompanyInfoByType(companyInfoType);
        return ApiResponse.success(SuccessStatus.GET_COMPANYINFO_SUCCESS, responseDTO);
    }
}
