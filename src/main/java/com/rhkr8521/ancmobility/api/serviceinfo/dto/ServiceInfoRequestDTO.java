package com.rhkr8521.ancmobility.api.serviceinfo.dto;

import com.rhkr8521.ancmobility.api.serviceinfo.entity.ServiceInfoType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceInfoRequestDTO {
    private ServiceInfoType serviceInfoType;
    private String title;
    private String subTitle;
    private String apply;
}