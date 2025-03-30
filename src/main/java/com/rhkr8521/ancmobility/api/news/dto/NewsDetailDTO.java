package com.rhkr8521.ancmobility.api.news.dto;

import com.rhkr8521.ancmobility.api.news.entity.News;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NewsDetailDTO {

    private Long id;
    private String title;
    private String author;
    private String content;
    private LocalDateTime createdAt;
    private long viewCnt;

    public NewsDetailDTO(News news) {
        this.id = news.getId();
        this.title = news.getTitle();
        this.author = news.getAuthor() != null ? news.getAuthor().getNickname() : "알 수 없음";
        this.content = news.getContent();
        this.createdAt = news.getCreatedAt();
        this.viewCnt = news.getViewCnt();
    }

    public static NewsDetailDTO from(News news) {
        return new NewsDetailDTO(news);
    }
}
