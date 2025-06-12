package com.rhkr8521.ancmobility.api.franchise.repository;

import com.rhkr8521.ancmobility.api.franchise.entity.Franchise;
import com.rhkr8521.ancmobility.api.news.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FranchiseRepository extends JpaRepository<Franchise, Long> {

    Optional<Franchise> findByNameAndPhoneNumber(String name, String phoneNumber);
    Optional<Franchise> findByPhoneNumber(String phoneNumber);
    Optional<Franchise> findById(Long id);
    Page<Franchise> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
