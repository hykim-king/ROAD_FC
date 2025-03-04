package com.pcwk.ehr.notice;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pcwk.ehr.DataNotFoundException;
import com.pcwk.ehr.notice.NoticeQuestion;
import com.pcwk.ehr.member.Member;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NoticeAnswerService {
	final Logger log = LoggerFactory.getLogger(getClass());
	
	private final NoticeAnswerRepository answerRepository;
	
//	public AnswerService() {
//		log.info("┌──────────────────────┐");
//		log.info("│ AnswerService()      │");
//		log.info("└──────────────────────┘");
//	}
	
	public void vote(NoticeAnswer answer, Member member) {
		answer.getVoter().add(member);
		
		answerRepository.save(answer);
	}
	
	public void delete(NoticeAnswer answer) {
		answerRepository.delete(answer);
	}
	
	public NoticeAnswer getAnswer(Integer id) {
		Optional<NoticeAnswer> answer = answerRepository.findById(id);
		
		if(answer.isPresent() == true) {
			return answer.get();
		}else {
			throw new DataNotFoundException();
		}
	}
	
	public NoticeAnswer modify(NoticeAnswer answer, String content) {
		answer.setContent(content);
		answer.setModifyDate(LocalDateTime.now());
		
		return answerRepository.save(answer);
	}
	
	public NoticeAnswer create(NoticeQuestion question, String content, Member author) {
		NoticeAnswer answer = new NoticeAnswer();
		answer.setContent(content);
		answer.setCreateDate(LocalDateTime.now());
		answer.setQuestion(question);
		answer.setAuthor(author);
		
		log.info("content:{}",content);
		log.info("question.getId:{}",question.getId());
		log.info("author:{}",author.getUsername());
		
		NoticeAnswer outVO = answerRepository.save(answer);
		log.info("outVO:{}",outVO.getId());
		
		return outVO;
	}
	
}
