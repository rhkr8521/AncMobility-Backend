package com.rhkr8521.ancmobility.api.term.service;

import com.rhkr8521.ancmobility.api.member.entity.Role;
import com.rhkr8521.ancmobility.api.term.dto.TermRequestDTO;
import com.rhkr8521.ancmobility.api.term.dto.TermResponseDTO;
import com.rhkr8521.ancmobility.api.term.entity.Term;
import com.rhkr8521.ancmobility.api.term.entity.TermType;
import com.rhkr8521.ancmobility.api.term.repository.TermRepository;
import com.rhkr8521.ancmobility.common.exception.BadRequestException;
import com.rhkr8521.ancmobility.common.exception.NotFoundException;
import com.rhkr8521.ancmobility.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TermService {

    private final TermRepository termRepository;

    // ADMIN 권한 체크
    private void validateAdmin(Role userRole) {
        if (userRole != Role.ADMIN) {
            throw new BadRequestException(ErrorStatus.NEED_ADMIN_ROLE_EXCEPTION.getMessage());
        }
    }

    @Transactional
    public void createTerm(TermRequestDTO dto, Role userRole) {
        // 관리자 권한 체크
        validateAdmin(userRole);

        // 약관 등록 여부 체크
        if (termRepository.findByTermType(dto.getTermType()).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_CREATE_TERM_EXCEPTION.getMessage());
        }

        Term term = Term.builder()
                .content(dto.getContent())
                .termType(dto.getTermType())
                .build();

        Term saved = termRepository.save(term);
        new TermResponseDTO(saved);
    }

    @Transactional
    public void updateTerm(TermRequestDTO dto, Role userRole) {
        // 관리자 권한 체크
        validateAdmin(userRole);

        // 약관 타입 체크
        Term term = termRepository.findByTermType(dto.getTermType())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.TERM_TYPE_NOTFOUND_EXCEPTION.getMessage()));

        term = term.toBuilder()
                .content(dto.getContent())
                .build();

        Term updated = termRepository.save(term);
        new TermResponseDTO(updated);
    }

    @Transactional(readOnly = true)
    public TermResponseDTO getTermByType(TermType termType) {

        // 약관 타입 체크
        Term term = termRepository.findByTermType(termType)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.TERM_TYPE_NOTFOUND_EXCEPTION.getMessage()));

        return new TermResponseDTO(term);
    }
}
