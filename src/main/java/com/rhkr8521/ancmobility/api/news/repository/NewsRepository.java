package com.rhkr8521.ancmobility.api.news.repository;

import com.rhkr8521.ancmobility.api.news.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    // 뉴스 목록 조회
    Page<News> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // 뉴스 조회수 증가
    @Modifying
    @Query("UPDATE News n SET n.viewCnt = n.viewCnt + 1 WHERE n.id = :id")
    void incrementViewCount(Long id);
}
