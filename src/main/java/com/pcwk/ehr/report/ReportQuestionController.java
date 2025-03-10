package com.pcwk.ehr.report;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.pcwk.ehr.member.Member;
import com.pcwk.ehr.member.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RequestMapping("/report/question")
@Controller
public class ReportQuestionController {
	
	final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	ReportQuestionService service;
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	ReportAnswerService answerService;
	
	private final String uploadDir = "D:/JAP_20240909/04_SPRING/BOOT/WORKSPACE/project_oracle/src/main/resources/static/img/report/";  // 이미지 저장할 경로 설정
	
	@PreAuthorize("isAuthenticated")
	@PostMapping("/create")
	public String questionCreate(@Valid ReportQuestionForm questionForm,BindingResult bindingResult,Principal principal) throws IllegalStateException, IOException {
		if(bindingResult.hasErrors()) {
			return "report/question/question_form";
		}
		Member member = memberService.getMember(principal.getName());
		MultipartFile[] files = questionForm.getImgFile();
        List<String> filePaths = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                File saveFile = new File(uploadDir + fileName);
                file.transferTo(saveFile);

                filePaths.add("/img/report/" + fileName);
            }
        }

        //여러 개의 파일 경로를 쉼표(,)로 구분하여 저장
        String savedFilePaths = String.join(",", filePaths);

        int questionId = service.create(questionForm.getSubject(), questionForm.getContent(), member, savedFilePaths);
		log.info("questionId:{}",questionId);
		return "redirect:/report/question/list";
	}
	
	@PreAuthorize("isAuthenticated")
	@GetMapping("/create")
	public String questionCreate(ReportQuestionForm questionForm,
			Model model, HttpServletRequest request) {
		model.addAttribute("currentUrl", request.getRequestURI());
		return "report/question/question_form";
	}
	
	@PreAuthorize("isAuthenticated")
	@PostMapping("/modify/{id}")
	public String questionModify(@Valid ReportQuestionForm questionForm, BindingResult bindingResult, 
			Principal principal, @PathVariable("id")Integer id, HttpServletRequest request, Model model) throws IllegalStateException, IOException {
		String viewName = "report/question/question_form";
		
		if(bindingResult.hasErrors()) {
			return viewName;
		}
		model.addAttribute("currentUrl", request.getRequestURI());
		ReportQuestion question = service.getQuestion(id);
		log.info("question:{}",question);
		log.info("question:{}",questionForm.getSubject());
		log.info("question:{}",questionForm.getContent());
		
		if(!question.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
		}
		
		MultipartFile[] files = questionForm.getImgFile();
        List<String> filePaths = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                File saveFile = new File(uploadDir + fileName);
                file.transferTo(saveFile);

                filePaths.add("/img/report/" + fileName);
            }
        }

        //여러 개의 파일 경로를 쉼표(,)로 구분하여 저장
        String savedFilePaths = String.join(",", filePaths);
        
		question.setModifyDate(LocalDateTime.now());
		
		ReportQuestion outQuestion = service.modify(question, questionForm.getSubject(), questionForm.getContent(), savedFilePaths);
		
		String returnUrl = String.format("redirect:/report/question/detail/%s",id);
		
		return returnUrl;
	}
	
	@PreAuthorize("isAuthenticated")
	@GetMapping("/modify/{id}")
	public String modify(ReportQuestionForm questionForm,@PathVariable("id")Integer id,Principal principal,
			HttpServletRequest request, Model model) {
		
		ReportQuestion question = service.getQuestion(id);
		log.info("question:{}",question);
		
		if(!question.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
		}
		
		questionForm.setSubject(question.getSubject());
		questionForm.setContent(question.getContent());
		model.addAttribute("currentUrl", request.getRequestURI());
		return "report/question/question_form";
	}
	
	@GetMapping(value="/detail/{id}")
	public String detail(Model model, @PathVariable("id") Integer id, ReportAnswerForm answerForm,
	                     @RequestParam(value = "page", defaultValue = "0") int page,
	                     @AuthenticationPrincipal UserDetails userDetails,
											 HttpServletRequest request) {

	    // 기본값 설정
	    String currentUsername = "anonymous"; 
	    int userGrade = 0; 

	    // 로그인한 경우에만 정보 가져오기
	    if (userDetails != null) {
	        currentUsername = userDetails.getUsername();
	        Member member = memberService.getMember(currentUsername);
	        userGrade = member.getUserGrade();
	    }

	    ReportQuestion question = service.getQuestion(id);
	    Page<ReportAnswer> paging = answerService.getPaging(id, page);

	    // 조회수 증가 (본인이 아닐 경우만)
	    if (!currentUsername.equals("anonymous") && !currentUsername.equals(question.getAuthor().getUsername())) {
	        service.increaseViewCount(id);
	    }

	    // 이전글 조회 (현재 id보다 작은 값 중 가장 큰 값)
	    ReportQuestion prevQuestion = service.getPreviousQuestion(id);
	    
	    // 다음글 조회 (현재 id보다 큰 값 중 가장 작은 값)
	    ReportQuestion nextQuestion = service.getNextQuestion(id);

	    model.addAttribute("prevQuestion", prevQuestion);
	    model.addAttribute("nextQuestion", nextQuestion);
	    model.addAttribute("question", question);
	    model.addAttribute("paging", paging);
	    model.addAttribute("userGrade", userGrade); // 비로그인 사용자는 "anonymous" 설정
			model.addAttribute("currentUrl", request.getRequestURI());

	    return "report/question/question_detail";
	}

	
	@PreAuthorize("isAuthenticated")
	@GetMapping("/vote/{id}")
	public String questionVote(@PathVariable("id")Integer id, Principal principal) {
		log.info("id:{}",id);
		log.info("principal.getName:{}",principal.getName());
		
		ReportQuestion question = service.getQuestion(id);
		Member member = memberService.getMember(principal.getName());
		
		service.vote(question, member);
		String returnUrl = String.format("redirect:/report/question/detail/%s", id);
		
		return returnUrl;
	}
	
	@PreAuthorize("isAuthenticated")
	@GetMapping("/delete/{id}")
	public String questionDelete(@PathVariable("id")Integer id, Principal principal) {
		ReportQuestion question = service.getQuestion(id);
		Member member = memberService.getMember(principal.getName());
		 if (!question.getAuthor().getUsername().equals(principal.getName()) && member.getUserGrade() != 1) {
		        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
		 }
		
		service.delete(question);
		
		return "redirect:/report/question/list";
	}
	
	@GetMapping(value = "/list")
	public String list(Model model,
			@RequestParam(value = "page", defaultValue = "0")int page
			,@RequestParam(value = "keyword", defaultValue = "")String keyword
			,HttpServletRequest request) {
		
		
		Page<ReportQuestion> paging = service.getList(page,keyword);
		
		model.addAttribute("paging",paging);
		model.addAttribute("keyword", keyword);
		model.addAttribute("page", "report");
		model.addAttribute("currentUrl", request.getRequestURI());
		log.info("size:"+paging.getSize());
		
		return "report/question/question_list";
	}
}
