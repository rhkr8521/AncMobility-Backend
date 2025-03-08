package com.rhkr8521.ancmobility.api.notice.dto;

import com.rhkr8521.ancmobility.api.notice.entity.Notice;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NoticeDetailDTO {

    private Long id;
    private String title;
    private String author;
    private String content;
    private LocalDateTime createdAt;
    private long viewCnt;

    public NoticeDetailDTO(Notice notice) {
        this.id = notice.getId();
        this.title = notice.getTitle();
        this.author = notice.getAuthor() != null ? notice.getAuthor().getNickname() : "알 수 없음";
        this.content = notice.getContent();
        this.createdAt = notice.getCreatedAt();
        this.viewCnt = notice.getViewCnt();
    }

    public static NoticeDetailDTO from(Notice notice) {
        return new NoticeDetailDTO(notice);
    }
}
