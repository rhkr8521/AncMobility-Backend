package com.rhkr8521.ancmobility.api.notice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeCreateDTO {

    private String title;
    private String content;

    public NoticeCreateDTO(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
