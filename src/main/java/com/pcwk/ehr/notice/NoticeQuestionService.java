package com.pcwk.ehr.notice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.pcwk.ehr.DataNotFoundException;
import com.pcwk.ehr.notice.NoticeAnswer;
import com.pcwk.ehr.member.Member;
import com.pcwk.ehr.member.MemberRepository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class NoticeQuestionService {
	final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	NoticeQuestionRepository questionRepository;
	
	public NoticeQuestionService() {
		log.info("┌──────────────────────┐");
		log.info("│ QuestionService()    │");
		log.info("└──────────────────────┘");
	}
	
	public Specification<NoticeQuestion> search(String keyword){
		
		return new Specification<>() {

			@Override
			public Predicate toPredicate(Root<NoticeQuestion> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				//query.distinct(true);
				
				//left outer join
				Join<NoticeQuestion, Member> u1 = root.join("author",JoinType.LEFT);
				Join<NoticeQuestion, NoticeAnswer>   a  = root.join("answerList",JoinType.LEFT);
				Join<NoticeAnswer,Member>    u2 = root.join("author", JoinType.LEFT);
				
				return criteriaBuilder.or(criteriaBuilder.like(root.get("subject"), "%"+keyword+"%"),//제목
										  criteriaBuilder.like(root.get("content"), "%"+keyword+"%"),//내용
										  criteriaBuilder.like(u1.get("username"), "%"+keyword+"%"), //질문작성자
										  criteriaBuilder.like(a.get("content"), "%"+keyword+"%"),  //답변내용
										  criteriaBuilder.like(u2.get("username"), "%"+keyword+"%") //답변 작성자
				);
			}
			
		};
	}
	
	public void vote(NoticeQuestion question, Member member) {
		question.getVoter().add(member);
		
		questionRepository.save(question);
	}
	
	public void delete(NoticeQuestion question) {
		log.info("┌──────────────────────┐");
		log.info("│ delete()    		 │");
		log.info("└──────────────────────┘");
		
		questionRepository.delete(question);
	}
	
	public NoticeQuestion modify(NoticeQuestion question, String subject, String content, String img) {
		log.info("┌──────────────────────┐");
		log.info("│ modify()    		 │");
		log.info("└──────────────────────┘");
		
		question.setSubject(subject);
		question.setContent(content);
		question.setModifyDate(LocalDateTime.now());
		question.setImg(img);
		
		NoticeQuestion q = questionRepository.save(question);
		log.info("수정된 정보:{}",q);
		return q;
	}
	
	public Page<NoticeQuestion> getList(int page, String keyword){
		log.info("┌──────────────────────┐");
		log.info("│ getList()    		 │");
		log.info("└──────────────────────┘");
		
		log.info("param:page{}",page);
		log.info("keyword:{}",keyword);
		
		//날짜 순으로
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));
		
		Pageable pageable = PageRequest.of(page, 10,Sort.by(sorts));
		
		Specification<NoticeQuestion> specification = search(keyword);
		
		return questionRepository.findAll(specification,pageable);
	}
	
	public int create(String subject,String content, Member author, String img) {
		log.info("┌──────────────────────┐");
		log.info("│ create()    		 │");
		log.info("└──────────────────────┘");
		
		log.info("1.param|subject:{} ",subject);
		log.info("1.param|content:{} ",content);
		
		NoticeQuestion question = new NoticeQuestion();
		question.setSubject(subject);
		question.setContent(content);
		question.setCreateDate(LocalDateTime.now());
		question.setAuthor(author);
		question.setImg(img);  // 이미지 파일명 저장
		
		NoticeQuestion outQuestion = questionRepository.save(question);
		
		return outQuestion.getId();
	}
	
    public void increaseViewCount(Integer questionId) {
        log.info("┌──────────────────────┐");
        log.info("│ increaseViewCount()  │");
        log.info("└──────────────────────┘");
        
        // 조회수 증가하기
        Optional<NoticeQuestion> questionOpt = questionRepository.findById(questionId);
        if(questionOpt.isPresent()) {
            NoticeQuestion question = questionOpt.get();
            question.setView_count(question.getView_count() + 1);  // 조회수 증가
            questionRepository.save(question);  // 저장
        } else {
            throw new RuntimeException("질문을 찾을 수 없습니다.");
        }
    }
	
	public NoticeQuestion getQuestion(Integer id) {
		log.info("┌──────────────────────┐");
		log.info("│ getQuestion()    	 │");
		log.info("└──────────────────────┘");
		
		log.info("1.param|id:{} ",id);
		Optional<NoticeQuestion> question = questionRepository.findById(id);
		if(question.isPresent() == true){
			return question.get();
		}else {
			throw new RuntimeException("데이터가 없습니다.");
			//throw new DataNotFoundException("질문 not found");
		}
	}
	
	public List<NoticeQuestion> getList(){
		log.info("┌──────────────┐");
		log.info("│ getList()    │");
		log.info("└──────────────┘");
		
		return questionRepository.findAll();
	}
	

	public NoticeQuestion getPreviousQuestion(Integer id) {
	    return questionRepository.findTopByIdLessThanOrderByIdDesc(id).orElse(null);
	}

	public NoticeQuestion getNextQuestion(Integer id) {
	    return questionRepository.findTopByIdGreaterThanOrderByIdAsc(id).orElse(null);
	}
}
