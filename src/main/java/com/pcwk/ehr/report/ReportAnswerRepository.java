package com.pcwk.ehr.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pcwk.ehr.member.Member;

public interface ReportAnswerRepository extends JpaRepository<ReportAnswer, Integer> {
	Page<ReportAnswer> findByAuthor(Member author, Pageable pageable);
}