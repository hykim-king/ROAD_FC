package com.pcwk.ehr.notice;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pcwk.ehr.member.Member;
//Table, PK:Type
public interface NoticeQuestionRepository extends JpaRepository<NoticeQuestion, Integer> {
	
	@Modifying
    @Query("DELETE FROM NoticeQuestion n WHERE n.author.id = :authorId")
    void deleteByAuthorId(@Param("authorId") Long authorId);
	
	//검색: 제목, 내용, 질문 작성자, 답변 내용, 답변 작성자
	Page<NoticeQuestion> findAll(Specification<NoticeQuestion> spec, Pageable pageable);
	
	@Override
	Page<NoticeQuestion> findAll(Pageable pageable) ;
	
	@Query("SELECT q FROM NoticeQuestion q WHERE q.subject LIKE %:keyword%")
	List<NoticeQuestion> findBySubjectContaining(@Param("keyword") String keyword);
	
	@Query(value = "SELECT *	"
			+ "  FROM Noticequestion 	"
			+ " WHERE subject LIKE %:subject%",nativeQuery = true)
	List<NoticeQuestion> findBySubjectContainingNative(@Param("subject") String subject);
	
	//subject like
	List<NoticeQuestion> findBySubjectLike(String subject);
	
	/**
	 * 제목으로 조회
	 * 기본 제공 사항이 아님
	 * @param subject
	 * @return
	 */
	//findBy+엔티티의 속성명:findBy+Subject
	NoticeQuestion findBySubject(String subject);
	
	//subject and content를 조회
	//where subject=? and content=?
	NoticeQuestion findBySubjectAndContent(String subject, String content);
	
}
