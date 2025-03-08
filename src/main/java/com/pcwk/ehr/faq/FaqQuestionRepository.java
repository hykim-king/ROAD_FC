package com.pcwk.ehr.faq;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
//Table, PK:Type
public interface FaqQuestionRepository extends JpaRepository<FaqQuestion, Integer> {
	
	@Modifying
    @Query("DELETE FROM FaqQuestion f WHERE f.author.id = :authorId")
    void deleteByAuthorId(@Param("authorId") Long authorId);
	
	//검색: 제목, 내용, 질문 작성자, 답변 내용, 답변 작성자
	Page<FaqQuestion> findAll(Specification<FaqQuestion> spec, Pageable pageable);
	
	@Override
	Page<FaqQuestion> findAll(Pageable pageable) ;
	
	@Query("SELECT q FROM FaqQuestion q WHERE q.subject LIKE %:keyword%")
	List<FaqQuestion> findBySubjectContaining(@Param("keyword") String keyword);
	
	@Query(value = "SELECT *	"
			+ "  FROM Faqquestion 	"
			+ " WHERE subject LIKE %:subject%",nativeQuery = true)
	List<FaqQuestion> findBySubjectContainingNative(@Param("subject") String subject);
	
	//subject like
	List<FaqQuestion> findBySubjectLike(String subject);
	
	/**
	 * 제목으로 조회
	 * 기본 제공 사항이 아님
	 * @param subject
	 * @return
	 */
	//findBy+엔티티의 속성명:findBy+Subject
	FaqQuestion findBySubject(String subject);
	
	//subject and content를 조회
	//where subject=? and content=?
	FaqQuestion findBySubjectAndContent(String subject, String content);
}
