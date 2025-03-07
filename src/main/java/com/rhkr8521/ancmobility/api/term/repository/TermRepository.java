package com.rhkr8521.ancmobility.api.term.repository;

import com.rhkr8521.ancmobility.api.term.entity.Term;
import com.rhkr8521.ancmobility.api.term.entity.TermType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TermRepository extends JpaRepository<Term, Long> {
    Optional<Term> findByTermType(TermType termType);
}
