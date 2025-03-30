package com.rhkr8521.ancmobility.api.alliance.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AllianceListResponseDTO {
    private List<AllianceResponseDTO> active;
    private List<AllianceResponseDTO> inactive;
}
