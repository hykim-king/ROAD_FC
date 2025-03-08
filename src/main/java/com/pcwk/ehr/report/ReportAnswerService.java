package com.pcwk.ehr.report;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.pcwk.ehr.DataNotFoundException;
import com.pcwk.ehr.report.ReportQuestion;
import com.pcwk.ehr.member.Member;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReportAnswerService {
	final Logger log = LoggerFactory.getLogger(getClass());
	
	private final ReportAnswerRepository answerRepository;
	
//	public AnswerService() {
//		log.info("┌──────────────────────┐");
//		log.info("│ AnswerService()      │");
//		log.info("└──────────────────────┘");
//	}
	
	
	public Page<ReportAnswer> getPaging(Integer questionId,int page){
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		return answerRepository.findByQuestionId(questionId,pageable);
	}
	
	public Page<ReportAnswer> getQuestionsByAuthor(Member author, int page) {
	    Pageable pageable = PageRequest.of(page, 10); // 페이지 크기와 함께 Pageable 객체 생성
	    Page<ReportAnswer> pagedAnswers = answerRepository.findByAuthor(author, pageable);
	
	    // 중복을 제거하는 로직 추가
	    Map<Integer, ReportAnswer> uniqueAnswersMap = pagedAnswers.getContent().stream()
	            .collect(Collectors.toMap(
	                    answer -> answer.getQuestion().getId(),
	                    answer -> answer,
	                    (existing, replacement) -> existing
	            ));
	
	    List<ReportAnswer> uniqueAnswersList = uniqueAnswersMap.values().stream().collect(Collectors.toList());
	
	    // 중복이 제거된 결과를 기반으로 새로운 Page 객체 생성
	    Page<ReportAnswer> uniquePagedAnswers = new PageImpl<>(uniqueAnswersList, pageable, uniqueAnswersList.size());
	
	    return uniquePagedAnswers;
	}
	
	public void vote(ReportAnswer answer, Member member) {
		answer.getVoter().add(member);
		
		answerRepository.save(answer);
	}
	
	public void delete(ReportAnswer answer) {
		answerRepository.delete(answer);
	}
	
	public ReportAnswer getAnswer(Integer id) {
		Optional<ReportAnswer> answer = answerRepository.findById(id);
		
		if(answer.isPresent() == true) {
			return answer.get();
		}else {
			throw new DataNotFoundException();
		}
	}
	
	public ReportAnswer modify(ReportAnswer answer, String content) {
		answer.setContent(content);
		answer.setModifyDate(LocalDateTime.now());
		
		return answerRepository.save(answer);
	}
	
	public ReportAnswer create(ReportQuestion question, String content, Member author) {
		ReportAnswer answer = new ReportAnswer();
		answer.setContent(content);
		answer.setCreateDate(LocalDateTime.now());
		answer.setQuestion(question);
		answer.setAuthor(author);
		
		log.info("content:{}",content);
		log.info("question.getId:{}",question.getId());
		log.info("author:{}",author.getUsername());
		
		ReportAnswer outVO = answerRepository.save(answer);
		log.info("outVO:{}",outVO.getId());
		
		return outVO;
	}
	
}
