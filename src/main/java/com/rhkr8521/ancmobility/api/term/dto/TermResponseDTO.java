package com.rhkr8521.ancmobility.api.term.dto;

import com.rhkr8521.ancmobility.api.term.entity.Term;
import com.rhkr8521.ancmobility.api.term.entity.TermType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TermResponseDTO {
    private Long id;
    private String content;
    private TermType termType;

    public TermResponseDTO(Long id, String content, TermType termType) {
        this.id = id;
        this.content = content;
        this.termType = termType;
    }

    public TermResponseDTO(Term term) {
        this.id = term.getId();
        this.content = term.getContent();
        this.termType = term.getTermType();
    }
}
