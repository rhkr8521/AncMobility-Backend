package com.rhkr8521.ancmobility.api.news.controller;

import com.rhkr8521.ancmobility.api.news.service.NewsService;
import com.rhkr8521.ancmobility.api.news.dto.*;
import com.rhkr8521.ancmobility.api.news.entity.News;
import com.rhkr8521.ancmobility.common.SecurityMember;
import com.rhkr8521.ancmobility.common.response.ApiResponse;
import com.rhkr8521.ancmobility.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "News", description = "뉴스 관련 API 입니다.")
@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @Operation(
            summary = "뉴스 목록 조회"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<NewsPageResponseDTO>> getNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        NewsPageResponseDTO response = newsService.getNews(page, size);
        return ApiResponse.success(SuccessStatus.SEND_NEWS_SUCCESS, response);
    }

    @Operation(
            summary = "뉴스 상세 조회"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NewsDetailDTO>> getNews(@PathVariable Long id) {
        NewsDetailDTO newsDetailDTO = newsService.getNews(id);
        return ApiResponse.success(SuccessStatus.SEND_NEWS_SUCCESS, newsDetailDTO);
    }

    @Operation(
            summary = "뉴스 등록"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createNews(
            @RequestBody NewsCreateDTO createDTO,
            @AuthenticationPrincipal SecurityMember securityMember
    ) {

        newsService.createNews(createDTO,securityMember.getId());
        return ApiResponse.success_only(SuccessStatus.CREATE_NEWS_SUCCESS);
    }

    @Operation(
            summary = "뉴스 수정"
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NewsDetailDTO>> updateNews(
            @PathVariable Long id,
            @RequestBody NewsCreateDTO newsCreateDTO,
            @AuthenticationPrincipal SecurityMember securityMember
    ) {

        News news = newsService.updateNews(id, newsCreateDTO, securityMember.getId());
        NewsDetailDTO newsDetailDTO = NewsDetailDTO.from(news);
        return ApiResponse.success(SuccessStatus.UPDATE_NEWS_SUCCESS, newsDetailDTO);
    }

    @Operation(
            summary = "뉴스 삭제"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNews(@PathVariable Long id,
                                                          @AuthenticationPrincipal SecurityMember securityMember) {

        newsService.deleteNews(id, securityMember.getId());
        return ApiResponse.success_only(SuccessStatus.DELETE_NEWS_SUCCESS);
    }
}
