package com.rhkr8521.ancmobility.api.notice.service;

import com.rhkr8521.ancmobility.api.notice.dto.NoticeCreateDTO;
import com.rhkr8521.ancmobility.api.notice.dto.NoticePageResponseDTO;
import com.rhkr8521.ancmobility.api.notice.dto.NoticeDetailDTO;
import com.rhkr8521.ancmobility.api.notice.dto.NoticeListDTO;
import com.rhkr8521.ancmobility.api.notice.entity.Notice;
import com.rhkr8521.ancmobility.api.notice.repository.NoticeRepository;
import com.rhkr8521.ancmobility.api.member.entity.Member;
import com.rhkr8521.ancmobility.api.member.repository.MemberRepository;
import com.rhkr8521.ancmobility.api.member.entity.Role;
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
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;

    // 공지사항 목록 조회
    @Transactional(readOnly = true)
    public NoticePageResponseDTO getNotices(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notice> notices = noticeRepository.findAllByOrderByCreatedAtDesc(pageable); // 최신순으로 정렬

        Page<NoticeListDTO> noticeListDTOs = notices.map(NoticeListDTO::from);

        return NoticePageResponseDTO.builder()
                .totalElements(notices.getTotalElements())
                .totalPages(notices.getTotalPages())
                .page(notices.getNumber())
                .size(notices.getSize())
                .content(noticeListDTOs.getContent())
                .build();
    }

    // 공지사항 상세 조회
    @Transactional(readOnly = true)
    public NoticeDetailDTO getNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOTICE_NOTFOUND_EXCEPTION.getMessage()));

        noticeRepository.incrementViewCount(id);

        return NoticeDetailDTO.from(notice);
    }

    // 공지사항 등록
    @Transactional
    public void createNotice(NoticeCreateDTO createDTO, Long userId) {

        // 사용자 권한 확인 (ADMIN만 등록 가능)
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        if (user.getRole() != Role.ADMIN) {
            throw new BadRequestException(ErrorStatus.NEED_ADMIN_ROLE_EXCEPTION.getMessage());
        }

        Notice notice = Notice.builder()
                .title(createDTO.getTitle())
                .content(createDTO.getContent())
                .author(user)
                .viewCnt(0)  // 기본 조회수는 0
                .build();

        noticeRepository.save(notice);
    }

    // 공지사항 수정
    @Transactional
    public Notice updateNotice(Long id, NoticeCreateDTO noticeCreateDTO, Long userId) {

        // 사용자 권한 확인 (ADMIN만 수정 가능)
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        if (user.getRole() != Role.ADMIN) {
            throw new BadRequestException(ErrorStatus.NEED_ADMIN_ROLE_EXCEPTION.getMessage());
        }

        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOTICE_NOTFOUND_EXCEPTION.getMessage()));

        notice = notice.toBuilder()
                .title(noticeCreateDTO.getTitle())
                .content(noticeCreateDTO.getContent())
                .build();

        return noticeRepository.save(notice);
    }

    // 공지사항 삭제
    @Transactional
    public void deleteNotice(Long id, Long userId) {

        // 사용자 권한 확인 (ADMIN만 삭제 가능)
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        if (user.getRole() != Role.ADMIN) {
            throw new BadRequestException(ErrorStatus.NEED_ADMIN_ROLE_EXCEPTION.getMessage());
        }

        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NOTICE_NOTFOUND_EXCEPTION.getMessage()));

        noticeRepository.delete(notice);
    }
}
