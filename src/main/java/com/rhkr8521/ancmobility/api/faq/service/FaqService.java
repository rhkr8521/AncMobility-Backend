package com.rhkr8521.ancmobility.api.faq.service;

import com.rhkr8521.ancmobility.api.member.entity.Member;
import com.rhkr8521.ancmobility.api.member.entity.Role;
import com.rhkr8521.ancmobility.api.member.repository.MemberRepository;
import com.rhkr8521.ancmobility.api.faq.dto.FaqCreateDTO;
import com.rhkr8521.ancmobility.api.faq.dto.FaqListDTO;
import com.rhkr8521.ancmobility.api.faq.dto.FaqPageResponseDTO;
import com.rhkr8521.ancmobility.api.faq.entity.Faq;
import com.rhkr8521.ancmobility.api.faq.repository.FaqRepository;
import com.rhkr8521.ancmobility.common.exception.BadRequestException;
import com.rhkr8521.ancmobility.common.exception.NotFoundException;
import com.rhkr8521.ancmobility.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FaqService {

    private final MemberRepository memberRepository;
    private final FaqRepository faqRepository;

    // FaQ 목록 조회
    public FaqPageResponseDTO getFaqs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Faq> faqs = faqRepository.findAllByOrderByCreatedAtDesc(pageable); // 최신순으로 정렬

        Page<FaqListDTO> qnaListDTOS = faqs.map(FaqListDTO::from);

        return FaqPageResponseDTO.builder()
                .totalElements(faqs.getTotalElements())
                .totalPages(faqs.getTotalPages())
                .page(faqs.getNumber())
                .size(faqs.getSize())
                .content(qnaListDTOS.getContent())
                .build();
    }

    // FaQ 등록
    @Transactional
    public void createFaq(FaqCreateDTO createDTO, Long userId) {

        // 사용자 권한 확인 (ADMIN만 등록 가능)
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        if (user.getRole() != Role.ADMIN) {
            throw new BadRequestException(ErrorStatus.NEED_ADMIN_ROLE_EXCEPTION.getMessage());
        }

        Faq faq = Faq.builder()
                .title(createDTO.getTitle())
                .content(createDTO.getContent())
                .build();

        faqRepository.save(faq);
    }

    // FaQ 수정
    @Transactional
    public void updateFaq(Long id, FaqCreateDTO faqCreateDTO, Long userId) {

        // 사용자 권한 확인 (ADMIN만 수정 가능)
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        if (user.getRole() != Role.ADMIN) {
            throw new BadRequestException(ErrorStatus.NEED_ADMIN_ROLE_EXCEPTION.getMessage());
        }

        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.FAQ_NOTFOUND_EXCEPTION.getMessage()));

        faq = faq.toBuilder()
                .title(faqCreateDTO.getTitle())
                .content(faqCreateDTO.getContent())
                .build();

        faqRepository.save(faq);
    }

    // FaQ 삭제
    @Transactional
    public void deleteFaq(Long id, Long userId) {

        // 사용자 권한 확인 (ADMIN만 삭제 가능)
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        if (user.getRole() != Role.ADMIN) {
            throw new BadRequestException(ErrorStatus.NEED_ADMIN_ROLE_EXCEPTION.getMessage());
        }

        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.FAQ_NOTFOUND_EXCEPTION.getMessage()));

        faqRepository.delete(faq);
    }
}
