package com.pcwk.ehr.qna;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QnaAnswerRepository extends JpaRepository<QnaAnswer, Integer> {
    @Modifying
    @Query("DELETE FROM QnaAnswer a WHERE a.question.id IN (SELECT q.id FROM QnaQuestion q WHERE q.author.id = :authorId)")
    void deleteByQuestionAuthorId(@Param("authorId") Long authorId);
}