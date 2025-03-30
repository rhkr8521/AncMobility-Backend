package com.rhkr8521.ancmobility.api.alliance.controller;

import com.rhkr8521.ancmobility.api.alliance.dto.AllianceCreateDTO;
import com.rhkr8521.ancmobility.api.alliance.dto.AllianceListResponseDTO;
import com.rhkr8521.ancmobility.api.alliance.dto.AllianceResponseDTO;
import com.rhkr8521.ancmobility.api.alliance.entity.Tag;
import com.rhkr8521.ancmobility.api.alliance.service.AllianceService;
import com.rhkr8521.ancmobility.common.SecurityMember;
import com.rhkr8521.ancmobility.common.response.ApiResponse;
import com.rhkr8521.ancmobility.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Alliance", description = "제휴 관련 API")
@RestController
@RequestMapping("/api/v1/alliance")
@RequiredArgsConstructor
public class AllianceController {

    private final AllianceService allianceService;

    @Operation(summary = "제휴 생성")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> createAlliance(
            @RequestParam("title") String title,
            @RequestParam("subTitle") String subTitle,
            @RequestParam("company") String company,
            @RequestParam("active") Boolean active,
            @RequestParam(value = "tags", required = false) String[] tags,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @AuthenticationPrincipal SecurityMember securityMember
    ) {
        // 클라이언트에서 전달받은 태그 문자열을 Tag enum으로 변환
        List<Tag> tagList = new ArrayList<>();
        if (tags != null) {
            for (String tagStr : tags) {
                for (Tag tag : Tag.values()) {
                    if (tag.getValue().equalsIgnoreCase(tagStr)) {
                        tagList.add(tag);
                        break;
                    }
                }
            }
        }

        AllianceCreateDTO dto = AllianceCreateDTO.builder()
                .title(title)
                .subTitle(subTitle)
                .company(company)
                .active(active)
                .tags(tagList)
                .build();

        allianceService.createAlliance(dto, imageFile, securityMember.getRole());
        return ApiResponse.success_only(SuccessStatus.CREATE_ALLIANCE_SUCCESS);
    }

    @Operation(summary = "제휴 수정")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> updateAlliance(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("subTitle") String subTitle,
            @RequestParam("company") String company,
            @RequestParam("active") Boolean active,
            @RequestParam(value = "tags", required = false) String[] tags,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @AuthenticationPrincipal SecurityMember securityMember
    ) {
        List<Tag> tagList = new ArrayList<>();
        if (tags != null) {
            for (String tagStr : tags) {
                for (Tag tag : Tag.values()) {
                    if (tag.getValue().equalsIgnoreCase(tagStr)) {
                        tagList.add(tag);
                        break;
                    }
                }
            }
        }

        AllianceCreateDTO dto = AllianceCreateDTO.builder()
                .title(title)
                .subTitle(subTitle)
                .company(company)
                .active(active)
                .tags(tagList)
                .build();

        allianceService.updateAlliance(id, dto, imageFile, securityMember.getRole());
        return ApiResponse.success_only(SuccessStatus.UPDATE_ALLIANCE_SUCCESS);
    }

    @Operation(summary = "제휴 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<AllianceListResponseDTO>> getAllAlliances() {
        AllianceListResponseDTO responseDTO = allianceService.getAllAlliances();
        return ApiResponse.success(SuccessStatus.GET_ALLIANCE_SUCCESS, responseDTO);
    }

    @Operation(summary = "제휴 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAlliance(
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityMember securityMember
    ) {
        allianceService.deleteAlliance(id, securityMember.getRole());
        return ApiResponse.success_only(SuccessStatus.DELETE_ALLIANCE_SUCCESS);
    }
}
