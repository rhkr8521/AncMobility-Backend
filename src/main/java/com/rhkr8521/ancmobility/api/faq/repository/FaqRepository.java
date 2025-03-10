package com.rhkr8521.ancmobility.api.faq.repository;

import com.rhkr8521.ancmobility.api.faq.entity.Faq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {

    // QnA 목록 조회
    Page<Faq> findAllByOrderByCreatedAtDesc(Pageable pageable);

}
