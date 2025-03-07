package com.rhkr8521.ancmobility.api.term.dto;

import com.rhkr8521.ancmobility.api.term.entity.TermType;
import lombok.Data;

@Data
public class TermRequestDTO {
    private String content;
    private TermType termType;
}
