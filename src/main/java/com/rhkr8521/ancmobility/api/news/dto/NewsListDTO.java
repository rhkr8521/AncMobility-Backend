package com.rhkr8521.ancmobility.api.news.dto;

import com.rhkr8521.ancmobility.api.news.entity.News;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NewsListDTO {

    private Long id;
    private String title;
    private String subTitle;
    private String image;
    private String author;
    private LocalDateTime createdAt;
    private long viewCnt;

    public NewsListDTO(News news) {
        this.id = news.getId();
        this.title = news.getTitle();
        this.author = news.getAuthor() != null ? news.getAuthor().getNickname() : "알 수 없음";
        this.createdAt = news.getCreatedAt();
        this.viewCnt = news.getViewCnt();
        this.subTitle = news.getSubTitle();
        this.image = news.getImage();
    }

    public static NewsListDTO from(News news) {
        return new NewsListDTO(news);
    }
}
