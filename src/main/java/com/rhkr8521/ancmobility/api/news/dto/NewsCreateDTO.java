package com.rhkr8521.ancmobility.api.news.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsCreateDTO {

    private String title;
    private String subTitle;
    private String content;

    public NewsCreateDTO(String title, String content, String subTitle) {
        this.title = title;
        this.content = content;
        this.subTitle = subTitle;
    }
}
