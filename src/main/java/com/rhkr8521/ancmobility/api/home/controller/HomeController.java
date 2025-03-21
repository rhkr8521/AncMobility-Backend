package com.rhkr8521.ancmobility.api.home.controller;

import com.rhkr8521.ancmobility.api.home.dto.HomeRequestDTO;
import com.rhkr8521.ancmobility.api.home.dto.HomeResponseDTO;
import com.rhkr8521.ancmobility.api.home.service.HomeService;
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

@Tag(name = "Home", description = "홈 정보 관련 API")
@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @Operation(summary = "홈 화면 정보 등록")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> createHomeInfo(
            @RequestParam("title") String title,
            @RequestParam("subTitle") String subTitle,
            @RequestParam(value = "bannerImage1", required = false) MultipartFile bannerImageFile1,
            @RequestParam(value = "bannerImage2", required = false) MultipartFile bannerImageFile2,
            @RequestParam(value = "bannerImage3", required = false) MultipartFile bannerImageFile3,
            @AuthenticationPrincipal SecurityMember securityMember
    ) {

        HomeRequestDTO dto = HomeRequestDTO.builder()
                .title(title)
                .subTitle(subTitle)
                .build();

        homeService.createHomeInfo(dto, bannerImageFile1, bannerImageFile2, bannerImageFile3, securityMember.getRole());
        return ApiResponse.success_only(SuccessStatus.SAVE_HOMEINFO_SUCCESS);
    }

    @Operation(summary = "홈 화면 정보 수정")
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> updateHomeInfo(
            @RequestParam("title") String title,
            @RequestParam("subTitle") String subTitle,
            @RequestParam(value = "bannerImage1", required = false) MultipartFile bannerImageFile1,
            @RequestParam(value = "bannerImage2", required = false) MultipartFile bannerImageFile2,
            @RequestParam(value = "bannerImage3", required = false) MultipartFile bannerImageFile3,
            @AuthenticationPrincipal SecurityMember securityMember
    ) {

        HomeRequestDTO dto = HomeRequestDTO.builder()
                .title(title)
                .subTitle(subTitle)
                .build();

        homeService.updateHomeInfo(dto, bannerImageFile1, bannerImageFile2, bannerImageFile3, securityMember.getRole());
        return ApiResponse.success_only(SuccessStatus.UPDATE_HOMEINFO_SUCCESS);
    }

    @Operation(summary = "홈 화면 정보 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<HomeResponseDTO>> getHomeInfo(
    ) {
        HomeResponseDTO responseDTO = homeService.getHomeInfo();
        return ApiResponse.success(SuccessStatus.GET_HOMEINFO_SUCCESS, responseDTO);
    }
}
