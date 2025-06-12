package com.rhkr8521.ancmobility.api.franchise.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SettlementListResponseDTO<T> {
    private long totalElements;
    private int totalPages;
    private int page;
    private int size;
    private List<T> content;
}
