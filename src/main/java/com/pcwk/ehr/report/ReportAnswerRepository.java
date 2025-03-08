package com.pcwk.ehr.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pcwk.ehr.member.Member;

public interface ReportAnswerRepository extends JpaRepository<ReportAnswer, Integer> {
	Page<ReportAnswer> findByAuthor(Member author, Pageable pageable);

	Page<ReportAnswer> findByQuestionId(@Param("questionId") Integer questionId, Pageable pageable);

	@Modifying
	@Query("DELETE FROM QnaAnswer a WHERE a.question.id IN (SELECT q.id FROM ReportQuestion q WHERE q.author.id = :authorId)")
    void deleteByQuestionAuthorId(@Param("authorId") Long authorId);
}