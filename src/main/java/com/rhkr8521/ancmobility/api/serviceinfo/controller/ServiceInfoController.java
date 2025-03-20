package com.rhkr8521.ancmobility.api.serviceinfo.controller;

import com.rhkr8521.ancmobility.api.serviceinfo.dto.ServiceInfoRequestDTO;
import com.rhkr8521.ancmobility.api.serviceinfo.dto.ServiceInfoResponseDTO;
import com.rhkr8521.ancmobility.api.serviceinfo.entity.ServiceInfoType;
import com.rhkr8521.ancmobility.api.serviceinfo.service.ServiceInfoService;
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

@Tag(name = "ServiceInfo", description = "서비스 정보 관련 API")
@RestController
@RequestMapping("/api/v1/serviceinfo")
@RequiredArgsConstructor
public class ServiceInfoController {

    private final ServiceInfoService serviceInfoService;

    @Operation(summary = "서비스 정보 등록")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> createServieInfo(
            @RequestParam("serviceInfoType") ServiceInfoType serviceInfoType,
            @RequestParam("title") String title,
            @RequestParam("subTitle") String subTitle,
            @RequestParam("apply") String apply,
            @RequestParam(value = "bannerImage", required = false) MultipartFile bannerImageFile,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @AuthenticationPrincipal SecurityMember securityMember
    ) {

        ServiceInfoRequestDTO dto = ServiceInfoRequestDTO.builder()
                .serviceInfoType(serviceInfoType)
                .title(title)
                .subTitle(subTitle)
                .apply(apply)
                .build();

        serviceInfoService.createServiceInfo(dto, bannerImageFile, imageFile, securityMember.getRole());
        return ApiResponse.success_only(SuccessStatus.SAVE_SERVICEINFO_SUCCESS);
    }

    @Operation(summary = "서비스 정보 수정")
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> updateServiceInfo(
            @RequestParam("serviceInfoType") ServiceInfoType serviceInfoType,
            @RequestParam("title") String title,
            @RequestParam("subTitle") String subTitle,
            @RequestParam("apply") String apply,
            @RequestParam(value = "bannerImage", required = false) MultipartFile bannerImageFile,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @AuthenticationPrincipal SecurityMember securityMember
    ) {

        ServiceInfoRequestDTO dto = ServiceInfoRequestDTO.builder()
                .serviceInfoType(serviceInfoType)
                .title(title)
                .subTitle(subTitle)
                .apply(apply)
                .build();

        serviceInfoService.updateServiceInfo(dto, bannerImageFile, imageFile, securityMember.getRole());
        return ApiResponse.success_only(SuccessStatus.UPDATE_SERVICEINFO_SUCCESS);
    }

    @Operation(summary = "서비스 정보 조회")
    @GetMapping("/{serviceInfoType}")
    public ResponseEntity<ApiResponse<ServiceInfoResponseDTO>> getServiceInfo(
            @PathVariable ServiceInfoType serviceInfoType
    ) {
        ServiceInfoResponseDTO responseDTO = serviceInfoService.getServiceInfoByType(serviceInfoType);
        return ApiResponse.success(SuccessStatus.GET_SERVICEINFO_SUCCESS, responseDTO);
    }
}
