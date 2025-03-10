package com.rhkr8521.ancmobility.api.faq.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FaqCreateDTO {

    private String title;
    private String content;

    public FaqCreateDTO(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
