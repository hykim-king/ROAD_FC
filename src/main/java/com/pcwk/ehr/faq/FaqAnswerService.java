package com.pcwk.ehr.faq;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pcwk.ehr.DataNotFoundException;
import com.pcwk.ehr.faq.FaqQuestion;
import com.pcwk.ehr.member.Member;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FaqAnswerService {
	final Logger log = LoggerFactory.getLogger(getClass());
	
	private final FaqAnswerRepository answerRepository;
	
//	public AnswerService() {
//		log.info("┌──────────────────────┐");
//		log.info("│ AnswerService()      │");
//		log.info("└──────────────────────┘");
//	}
	
	public void vote(FaqAnswer answer, Member member) {
		answer.getVoter().add(member);
		
		answerRepository.save(answer);
	}
	
	public void delete(FaqAnswer answer) {
		answerRepository.delete(answer);
	}
	
	public FaqAnswer getAnswer(Integer id) {
		Optional<FaqAnswer> answer = answerRepository.findById(id);
		
		if(answer.isPresent() == true) {
			return answer.get();
		}else {
			throw new DataNotFoundException();
		}
	}
	
	public FaqAnswer modify(FaqAnswer answer, String content) {
		answer.setContent(content);
		answer.setModifyDate(LocalDateTime.now());
		
		return answerRepository.save(answer);
	}
	
	public FaqAnswer create(FaqQuestion question, String content, Member author) {
		FaqAnswer answer = new FaqAnswer();
		answer.setContent(content);
		answer.setCreateDate(LocalDateTime.now());
		answer.setQuestion(question);
		answer.setAuthor(author);
		
		log.info("content:{}",content);
		log.info("question.getId:{}",question.getId());
		log.info("author:{}",author.getUsername());
		
		FaqAnswer outVO = answerRepository.save(answer);
		log.info("outVO:{}",outVO.getId());
		
		return outVO;
	}
	
}
