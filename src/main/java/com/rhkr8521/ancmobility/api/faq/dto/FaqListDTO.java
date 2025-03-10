package com.rhkr8521.ancmobility.api.faq.dto;

import com.rhkr8521.ancmobility.api.faq.entity.Faq;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FaqListDTO {

    private Long id;
    private String title;
    private String content;
    public FaqListDTO(Faq faq) {
        this.id = faq.getId();
        this.title = faq.getTitle();
        this.content = faq.getContent();
    }

    public static FaqListDTO from(Faq faq) {
        return new FaqListDTO(faq);
    }
}
