package com.rhkr8521.ancmobility.api.alliance.dto;

import com.rhkr8521.ancmobility.api.alliance.entity.Tag;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllianceCreateDTO {
    private String title;
    private String subTitle;
    private String company;
    private Boolean active;
    private List<Tag> tags;
}
