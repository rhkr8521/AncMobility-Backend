package com.rhkr8521.ancmobility.api.news.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class NewsPageResponseDTO {
    private long totalElements;       // 전체 요소 수
    private int totalPages;           // 전체 페이지 수
    private int page;                 // 현재 페이지 (0부터 시작)
    private int size;                 // 요청한 페이지 사이즈
    private List<NewsListDTO> content; // 뉴스 목록 (최신순으로 반환)
}
