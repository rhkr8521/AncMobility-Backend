package com.rhkr8521.ancmobility.api.notice.controller;

import com.rhkr8521.ancmobility.api.notice.dto.*;
import com.rhkr8521.ancmobility.api.notice.entity.Notice;
import com.rhkr8521.ancmobility.api.notice.service.NoticeService;
import com.rhkr8521.ancmobility.common.SecurityMember;
import com.rhkr8521.ancmobility.common.response.ApiResponse;
import com.rhkr8521.ancmobility.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notice", description = "공지사항 관련 API 입니다.")
@RestController
@RequestMapping("/api/v1/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(
            summary = "공지사항 목록 조회"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<NoticePageResponseDTO>> getNotices(
            @RequestParam(defaultValue = "0") int page,   // 기본값 0
            @RequestParam(defaultValue = "10") int size    // 기본값 10
    ) {
        NoticePageResponseDTO response = noticeService.getNotices(page, size);
        return ApiResponse.success(SuccessStatus.SEND_NOTICE_SUCCESS, response);
    }

    @Operation(
            summary = "공지사항 상세 조회"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NoticeDetailDTO>> getNotice(@PathVariable Long id) {
        NoticeDetailDTO noticeDetailDTO = noticeService.getNotice(id);
        return ApiResponse.success(SuccessStatus.SEND_NOTICE_SUCCESS, noticeDetailDTO);
    }

    @Operation(
            summary = "공지사항 등록"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createNotice(
            @RequestBody NoticeCreateDTO createDTO,
            @AuthenticationPrincipal SecurityMember securityMember
    ) {

        noticeService.createNotice(createDTO,securityMember.getId());
        return ApiResponse.success_only(SuccessStatus.CREATE_NOTICE_SUCCESS);
    }

    @Operation(
            summary = "공지사항 수정"
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NoticeDetailDTO>> updateNotice(
            @PathVariable Long id,
            @RequestBody NoticeCreateDTO noticeCreateDTO,
            @AuthenticationPrincipal SecurityMember securityMember
    ) {

        Notice notice = noticeService.updateNotice(id, noticeCreateDTO, securityMember.getId());
        NoticeDetailDTO noticeDetailDTO = NoticeDetailDTO.from(notice);
        return ApiResponse.success(SuccessStatus.UPDATE_NOTICE_SUCCESS, noticeDetailDTO);
    }

    @Operation(
            summary = "공지사항 삭제"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(@PathVariable Long id,
                                                          @AuthenticationPrincipal SecurityMember securityMember) {

        noticeService.deleteNotice(id, securityMember.getId());
        return ApiResponse.success_only(SuccessStatus.DELETE_NOTICE_SUCCESS);
    }
}
