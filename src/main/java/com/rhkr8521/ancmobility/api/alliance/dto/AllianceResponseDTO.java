package com.rhkr8521.ancmobility.api.alliance.dto;

import com.rhkr8521.ancmobility.api.alliance.entity.Alliance;
import com.rhkr8521.ancmobility.api.alliance.entity.Tag;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllianceResponseDTO {
    private Long id;
    private String title;
    private String subTitle;
    private String company;
    private String image;
    private Boolean active;
    private List<Tag> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AllianceResponseDTO fromEntity(Alliance alliance) {
        return AllianceResponseDTO.builder()
                .id(alliance.getId())
                .title(alliance.getTitle())
                .subTitle(alliance.getSubTitle())
                .company(alliance.getCompany())
                .image(alliance.getImage())
                .active(alliance.getActive())
                .tags(alliance.getTags())
                .createdAt(alliance.getCreatedAt())
                .updatedAt(alliance.getUpdatedAt())
                .build();
    }
}
