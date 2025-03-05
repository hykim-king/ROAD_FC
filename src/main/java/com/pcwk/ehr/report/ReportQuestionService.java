package com.pcwk.ehr.report;

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
import com.pcwk.ehr.report.ReportAnswer;
import com.pcwk.ehr.member.Member;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class ReportQuestionService {
	final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	ReportQuestionRepository questionRepository;
	
	public ReportQuestionService() {
		log.info("┌──────────────────────┐");
		log.info("│ QuestionService()    │");
		log.info("└──────────────────────┘");
	}
	
	public Specification<ReportQuestion> search(String keyword){
		
		return new Specification<>() {

			@Override
			public Predicate toPredicate(Root<ReportQuestion> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				//query.distinct(true);
				
				//left outer join
				Join<ReportQuestion, Member> u1 = root.join("author",JoinType.LEFT);
				Join<ReportQuestion, ReportAnswer>   a  = root.join("answerList",JoinType.LEFT);
				Join<ReportAnswer,Member>    u2 = root.join("author", JoinType.LEFT);
				
				return criteriaBuilder.or(criteriaBuilder.like(root.get("subject"), "%"+keyword+"%"),//제목
										  criteriaBuilder.like(root.get("content"), "%"+keyword+"%"),//내용
										  criteriaBuilder.like(u1.get("username"), "%"+keyword+"%"), //질문작성자
										  criteriaBuilder.like(a.get("content"), "%"+keyword+"%"),  //답변내용
										  criteriaBuilder.like(u2.get("username"), "%"+keyword+"%") //답변 작성자
				);
			}
			
		};
	}
	

	public Page<ReportQuestion> getQuestionsByAuthor(Member author, int page) {
	    Pageable pageable = PageRequest.of(page, 10); // 페이지 크기와 함께 Pageable 객체 생성
	    return questionRepository.findByAuthor(author, pageable);
	}
	
	public void vote(ReportQuestion question, Member member) {
		log.info("┌──────────────────────┐");
		log.info("│ vote()       		 │");
		log.info("└──────────────────────┘");
		question.getVoter().add(member);
		
		questionRepository.save(question);
	}
	
	public void delete(ReportQuestion question) {
		log.info("┌──────────────────────┐");
		log.info("│ delete()    		 │");
		log.info("└──────────────────────┘");
		
		questionRepository.delete(question);
	}
	
	public ReportQuestion modify(ReportQuestion question, String subject, String content, String img) {
		log.info("┌──────────────────────┐");
		log.info("│ modify()    		 │");
		log.info("└──────────────────────┘");
		
		question.setSubject(subject);
		question.setContent(content);
		question.setModifyDate(LocalDateTime.now());
		question.setImg(img);
		
		ReportQuestion q = questionRepository.save(question);
		log.info("수정된 정보:{}",q);
		return q;
	}
	
	public Page<ReportQuestion> getList(int page){
		log.info("┌──────────────────────┐");
		log.info("│ getList()    		 │");
		log.info("└──────────────────────┘");
		
		log.info("param:page{}",page);
		
		//날짜 순으로
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));
		
		Pageable pageable = PageRequest.of(page, 10,Sort.by(sorts));
		
		return questionRepository.findAll(pageable);
	}
	
	public Page<ReportQuestion> getList(int page, String keyword){
		log.info("┌──────────────────────┐");
		log.info("│ getList()    		 │");
		log.info("└──────────────────────┘");
		
		log.info("param:page{}",page);
		log.info("keyword:{}",keyword);
		
		//날짜 순으로
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));
		
		Pageable pageable = PageRequest.of(page, 10,Sort.by(sorts));
		
		Specification<ReportQuestion> specification = search(keyword);
		
		return questionRepository.findAll(specification,pageable);
	}
	
	public int create(String subject,String content, Member author, String img) {
		log.info("┌──────────────────────┐");
		log.info("│ create()    		 │");
		log.info("└──────────────────────┘");
		
		log.info("1.param|subject:{} ",subject);
		log.info("1.param|content:{} ",content);
		
		ReportQuestion question = new ReportQuestion();
		question.setSubject(subject);
		question.setContent(content);
		question.setCreateDate(LocalDateTime.now());
		question.setAuthor(author);
		question.setImg(img);  // 이미지 파일명 저장
		
		ReportQuestion outQuestion = questionRepository.save(question);
		
		return outQuestion.getId();
	}
	
    public void increaseViewCount(Integer questionId) {
        log.info("┌──────────────────────┐");
        log.info("│ increaseViewCount()  │");
        log.info("└──────────────────────┘");
        
        // 조회수 증가하기
        Optional<ReportQuestion> questionOpt = questionRepository.findById(questionId);
        if(questionOpt.isPresent()) {
            ReportQuestion question = questionOpt.get();
            question.setView_count(question.getView_count() + 1);  // 조회수 증가
            questionRepository.save(question);  // 저장
        } else {
            throw new RuntimeException("질문을 찾을 수 없습니다.");
        }
    }
	
	public ReportQuestion getQuestion(Integer id) {
		log.info("┌──────────────────────┐");
		log.info("│ getQuestion()    	 │");
		log.info("└──────────────────────┘");
		
		log.info("1.param|id:{} ",id);
		Optional<ReportQuestion> question = questionRepository.findById(id);
		if(question.isPresent() == true){
			return question.get();
		}else {
			throw new RuntimeException("데이터가 없습니다.");
			//throw new DataNotFoundException("질문 not found");
		}
	}
	
	public List<ReportQuestion> getList(){
		log.info("┌──────────────┐");
		log.info("│ getList()    │");
		log.info("└──────────────┘");
		
		return questionRepository.findAll();
	}
	
	
}
