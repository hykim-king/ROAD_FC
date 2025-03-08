package com.pcwk.ehr.combine;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pcwk.ehr.member.Member;
import com.pcwk.ehr.qna.QnaQuestion;
import com.pcwk.ehr.qna.QnaQuestionService;
import com.pcwk.ehr.report.ReportQuestion;
import com.pcwk.ehr.report.ReportQuestionService;

//CombinedService.java
//CombinedService.java
@Service
public class CombinedService {
 @Autowired
 private ReportQuestionService reportQuestionService;

 @Autowired
 private QnaQuestionService qnaQuestionService;

 public Page<CombinedQuestion> getCombinedQuestions(Member author, int page) {
	    List<CombinedQuestion> combinedQuestions = new ArrayList<>();

	    // ReportQuestions를 CombinedQuestion으로 변환
	    Page<ReportQuestion> reportQuestions = reportQuestionService.getQuestionsByAuthor(author, page);
	    for (ReportQuestion rq : reportQuestions) {
	        CombinedQuestion cq = new CombinedQuestion();
	        cq.setId(rq.getId());
	        cq.setSubject(rq.getSubject());
	        cq.setContent(rq.getContent());
	        cq.setType("report");
	        combinedQuestions.add(cq);
	    }

	    // QnaQuestions를 CombinedQuestion으로 변환
	    Page<QnaQuestion> qnaQuestions = qnaQuestionService.getQuestionsByAuthor(author, page);
	    for (QnaQuestion qq : qnaQuestions) {
	        CombinedQuestion cq = new CombinedQuestion();
	        cq.setId(qq.getId());
	        cq.setSubject(qq.getSubject());
	        cq.setContent(qq.getContent());
	        cq.setType("qna");
	        combinedQuestions.add(cq);
	    }

	    // 페이지 정보 반환
	    long totalElements = reportQuestions.getTotalElements() + qnaQuestions.getTotalElements();
	    Pageable pageable = PageRequest.of(page, 10);
	    return new PageImpl<>(combinedQuestions, pageable, totalElements);
	}
}