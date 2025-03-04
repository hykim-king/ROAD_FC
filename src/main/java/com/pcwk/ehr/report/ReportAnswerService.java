package com.pcwk.ehr.report;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
