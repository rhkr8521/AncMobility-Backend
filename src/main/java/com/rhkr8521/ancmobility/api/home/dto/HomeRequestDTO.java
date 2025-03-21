package com.rhkr8521.ancmobility.api.home.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeRequestDTO {

    private String title;
    private String subTitle;
}
