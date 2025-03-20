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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "CompanyInfo", description = "회사 정보 관련 API")
@RestController
@RequestMapping("/api/v1/companyinfo")
@RequiredArgsConstructor
public class CompanyInfoController {

    private final CompanyInfoService companyInfoService;

    @Operation(summary = "회사 정보 등록")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> createCompanyInfo(
            @RequestParam("companyInfoType") CompanyInfoType companyInfoType,
            @RequestParam("title") String title,
            @RequestParam("subTitle") String subTitle,
            @RequestParam(value = "bannerImage", required = false) MultipartFile bannerImageFile,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @AuthenticationPrincipal SecurityMember securityMember
    ) {

        CompanyInfoRequestDTO dto = CompanyInfoRequestDTO.builder()
                .companyInfoType(companyInfoType)
                .title(title)
                .subTitle(subTitle)
                .build();

        companyInfoService.createCompanyInfo(dto, bannerImageFile, imageFile, securityMember.getRole());
        return ApiResponse.success_only(SuccessStatus.SAVE_COMPANYINFO_SUCCESS);
    }

    @Operation(summary = "회사 정보 수정")
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> updateCompanyInfo(
            @RequestParam("companyInfoType") CompanyInfoType companyInfoType,
            @RequestParam("title") String title,
            @RequestParam("subTitle") String subTitle,
            @RequestParam(value = "bannerImage", required = false) MultipartFile bannerImageFile,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @AuthenticationPrincipal SecurityMember securityMember
    ) {

        CompanyInfoRequestDTO dto = CompanyInfoRequestDTO.builder()
                .companyInfoType(companyInfoType)
                .title(title)
                .subTitle(subTitle)
                .build();

        companyInfoService.updateCompanyInfo(dto, bannerImageFile, imageFile, securityMember.getRole());
        return ApiResponse.success_only(SuccessStatus.UPDATE_COMPANYINFO_SUCCESS);
    }

    @Operation(summary = "회사 정보 조회")
    @GetMapping("/{companyInfoType}")
    public ResponseEntity<ApiResponse<CompanyInfoResponseDTO>> getCompanyInfo(
            @PathVariable CompanyInfoType companyInfoType
    ) {
        CompanyInfoResponseDTO responseDTO = companyInfoService.getCompanyInfoByType(companyInfoType);
        return ApiResponse.success(SuccessStatus.GET_COMPANYINFO_SUCCESS, responseDTO);
    }
}
