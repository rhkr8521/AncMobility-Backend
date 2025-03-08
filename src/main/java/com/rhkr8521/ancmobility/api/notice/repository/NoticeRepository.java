package com.rhkr8521.ancmobility.api.notice.repository;

import com.rhkr8521.ancmobility.api.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 공지사항 목록 조회
    Page<Notice> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // 공지사항 조회수 증가
    @Modifying
    @Query("UPDATE Notice n SET n.viewCnt = n.viewCnt + 1 WHERE n.id = :id")
    void incrementViewCount(Long id);
}
