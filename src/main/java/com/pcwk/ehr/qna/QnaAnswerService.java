package com.pcwk.ehr.qna;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pcwk.ehr.DataNotFoundException;
import com.pcwk.ehr.qna.QnaQuestion;
import com.pcwk.ehr.member.Member;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QnaAnswerService {
	final Logger log = LoggerFactory.getLogger(getClass());
	
	private final QnaAnswerRepository answerRepository;
	
//	public AnswerService() {
//		log.info("┌──────────────────────┐");
//		log.info("│ AnswerService()      │");
//		log.info("└──────────────────────┘");
//	}
	
	public void vote(QnaAnswer answer, Member member) {
		answer.getVoter().add(member);
		
		answerRepository.save(answer);
	}
	
	public void delete(QnaAnswer answer) {
		answerRepository.delete(answer);
	}
	
	public QnaAnswer getAnswer(Integer id) {
		Optional<QnaAnswer> answer = answerRepository.findById(id);
		
		if(answer.isPresent() == true) {
			return answer.get();
		}else {
			throw new DataNotFoundException();
		}
	}
	
	public QnaAnswer modify(QnaAnswer answer, String content) {
		answer.setContent(content);
		answer.setModifyDate(LocalDateTime.now());
		
		return answerRepository.save(answer);
	}
	
	public QnaAnswer create(QnaQuestion question, String content, Member author) {
		QnaAnswer answer = new QnaAnswer();
		answer.setContent(content);
		answer.setCreateDate(LocalDateTime.now());
		answer.setQuestion(question);
		answer.setAuthor(author);
		
		log.info("content:{}",content);
		log.info("question.getId:{}",question.getId());
		log.info("author:{}",author.getUsername());
		
		QnaAnswer outVO = answerRepository.save(answer);
		log.info("outVO:{}",outVO.getId());
		
		return outVO;
	}
	
}
