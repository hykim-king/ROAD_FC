package com.pcwk.ehr.report;

import java.security.Principal;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.pcwk.ehr.report.ReportQuestion;
import com.pcwk.ehr.report.ReportQuestionService;
import com.pcwk.ehr.member.Member;
import com.pcwk.ehr.member.MemberService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/report/answer")
public class ReportAnswerController {
	final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	ReportAnswerService aservice;
	
	@Autowired
	ReportQuestionService qservice;
	
	@Autowired
	MemberService memberService;
	
	@PreAuthorize("isAuthenticated")
	@GetMapping("/vote/{id}")
	public String answerVote(@PathVariable("id")Integer id, Principal principal) {
		
		ReportAnswer answer = aservice.getAnswer(id);
		Member member = memberService.getMember(principal.getName());
		
		aservice.vote(answer, member);
		
		String returnUrl = String.format("redirect:/report/question/detail/%s#answer_%s", answer.getQuestion().getId(),answer.getId());
		return returnUrl;
	}
	
	@PreAuthorize("isAuthenticated")
	@GetMapping("/delete/{id}")
	public String questionDelete(@PathVariable("id")Integer id, Principal principal) {
		ReportAnswer answer = aservice.getAnswer(id);
		Member member = memberService.getMember(principal.getName());
		
		if(!answer.getAuthor().getUsername().equals(principal.getName())&& member.getUserGrade() != 1) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
		}
		
		aservice.delete(answer);
		String returnUrl = String.format("redirect:/report/question/detail/%s", answer.getQuestion().getId());
		
		return returnUrl;
	}
	
	@PreAuthorize("isAuthenticated")
	@PostMapping("/modify/{id}")
	public String answerModify(@PathVariable("id")Integer id,@Valid ReportAnswerForm answerForm,
			BindingResult bindingResult,Principal principal) {
		
		if(bindingResult.hasErrors() == true) {
			return "report/answer/answer_form";
		}
		
		ReportAnswer answer = aservice.getAnswer(id);
		if(!answer.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
		}
		
		String content = answerForm.getContent();
		answer.setModifyDate(LocalDateTime.now());
		aservice.modify(answer, content);
		
		String returnUrl = String.format("redirect:/report/question/detail/%s#answer_%s", answer.getQuestion().getId(),answer.getId());
		
		return returnUrl;
	}
	
	@PreAuthorize("isAuthenticated")
	@GetMapping("/modify/{id}")
	public String answerModify(@PathVariable("id")Integer id,Principal principal,ReportAnswerForm answerForm) {
		log.info("┌──────────────────────┐");
		log.info("│ answerModify()       │");
		log.info("└──────────────────────┘");
		 
		ReportAnswer answer = aservice.getAnswer(id);
		
		if(!answer.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
		}
		
		answerForm.setContent(answer.getContent());
		
		return "report/answer/answer_form";
	}
	
	//Principal : 스프링 시큐리티가 제공하는 Principal객체
	@PreAuthorize("isAuthenticated")
	@PostMapping("/create/{id}")
	public String create(@PathVariable("id")Integer id, @Valid ReportAnswerForm answerForm, BindingResult bindingResult,
			Model model, Principal principal) {
		
		//TODO : AnswerService
		log.info("┌──────────────────────┐");
		log.info("│ create()    		 │");
		log.info("└──────────────────────┘");
		
		log.info("id:{}",id);
		ReportQuestion question = qservice.getQuestion(id);
		
		log.info("principal.getName():{}",principal.getName());
		Member member = memberService.getMember(principal.getName());
		
		log.info("siteUser:{}",member.getEmail());
		
		if(bindingResult.hasErrors() == true) {
			model.addAttribute("question",question);
			return "report/question/question_detail";
		}
		
		log.info("Content:{}", answerForm.getContent());
		ReportAnswer answer = aservice.create(question, answerForm.getContent(), member);
		
		String returnUrl = String.format("redirect:/report/question/detail/%s#answer_%s", id,answer.getId());
		
		return returnUrl;
	}
}
