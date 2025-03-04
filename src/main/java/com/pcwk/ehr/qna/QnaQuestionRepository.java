package com.pcwk.ehr.qna;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
//Table, PK:Type
public interface QnaQuestionRepository extends JpaRepository<QnaQuestion, Integer> {
	
	//검색: 제목, 내용, 질문 작성자, 답변 내용, 답변 작성자
	Page<QnaQuestion> findAll(Specification<QnaQuestion> spec, Pageable pageable);
	
	@Override
	Page<QnaQuestion> findAll(Pageable pageable) ;
	
	@Query("SELECT q FROM QnaQuestion q WHERE q.subject LIKE %:keyword%")
	List<QnaQuestion> findBySubjectContaining(@Param("keyword") String keyword);
	
	@Query(value = "SELECT *	"
			+ "  FROM Qnaquestion 	"
			+ " WHERE subject LIKE %:subject%",nativeQuery = true)
	List<QnaQuestion> findBySubjectContainingNative(@Param("subject") String subject);
	
	//subject like
	List<QnaQuestion> findBySubjectLike(String subject);
	
	/**
	 * 제목으로 조회
	 * 기본 제공 사항이 아님
	 * @param subject
	 * @return
	 */
	//findBy+엔티티의 속성명:findBy+Subject
	QnaQuestion findBySubject(String subject);
	
	//subject and content를 조회
	//where subject=? and content=?
	QnaQuestion findBySubjectAndContent(String subject, String content);
}
