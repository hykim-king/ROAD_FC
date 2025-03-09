package com.pcwk.ehr.qna;

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

import com.pcwk.ehr.qna.QnaAnswerForm;
import com.pcwk.ehr.member.Member;
import com.pcwk.ehr.member.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RequestMapping("/qna/question")
@Controller
public class QnaQuestionController {
	
	final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	QnaQuestionService service;
	
	@Autowired
	MemberService memberService;
	
	private final String uploadDir = "C:/Users/82109/Desktop/JAP_20240909/04_SPRING/boot/workspace/project_oracle/src/main/resources/static/img/qna/";  // 이미지 저장할 경로 설정
	
	@PreAuthorize("isAuthenticated")
	@PostMapping("/create")
	public String questionCreate(@Valid QnaQuestionForm questionForm,BindingResult bindingResult,Principal principal) throws IllegalStateException, IOException {
		if(bindingResult.hasErrors()) {
			return "qna/question/question_form";
		}
		Member member = memberService.getMember(principal.getName());
		MultipartFile[] files = questionForm.getImgFile();
        List<String> filePaths = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                File saveFile = new File(uploadDir + fileName);
                file.transferTo(saveFile);

                filePaths.add("/img/qna/" + fileName);
            }
        }

        //여러 개의 파일 경로를 쉼표(,)로 구분하여 저장
        String savedFilePaths = String.join(",", filePaths);

        int questionId = service.create(questionForm.getSubject(), questionForm.getContent(), member, savedFilePaths);
		log.info("questionId:{}",questionId);
		return "redirect:/qna/question/list";
	}
	
	@PreAuthorize("isAuthenticated")
	@GetMapping("/create")
	public String questionCreate(QnaQuestionForm questionForm,
			Model model, HttpServletRequest request) {
		model.addAttribute("currentUrl", request.getRequestURI());
		return "qna/question/question_form";
	}
	
	@PreAuthorize("isAuthenticated")
	@PostMapping("/modify/{id}")
	public String questionModify(@Valid QnaQuestionForm questionForm, BindingResult bindingResult, 
			Principal principal, @PathVariable("id")Integer id) throws IllegalStateException, IOException {
		String viewName = "qna/question/question_form";
		
		if(bindingResult.hasErrors()) {
			return viewName;
		}
		
		QnaQuestion question = service.getQuestion(id);
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

                filePaths.add("/img/qna/" + fileName);
            }
        }

        //여러 개의 파일 경로를 쉼표(,)로 구분하여 저장
        String savedFilePaths = String.join(",", filePaths);
        
		question.setModifyDate(LocalDateTime.now());
		
		QnaQuestion outQuestion = service.modify(question, questionForm.getSubject(), questionForm.getContent(), savedFilePaths);
		
		String returnUrl = String.format("redirect:/qna/question/detail/%s",id);
		
		return returnUrl;
	}
	
	@PreAuthorize("isAuthenticated")
	@GetMapping("/modify/{id}")
	public String modify(QnaQuestionForm questionForm,@PathVariable("id")Integer id,Principal principal) {
		
		QnaQuestion question = service.getQuestion(id);
		log.info("question:{}",question);
		
		if(!question.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
		}
		
		questionForm.setSubject(question.getSubject());
		questionForm.setContent(question.getContent());
		
		return "qna/question/question_form";
	}
	
	@GetMapping(value="/detail/{id}")
	public String detail(Model model,@PathVariable("id") Integer id,QnaAnswerForm answerForm,
			Principal principal,
			@AuthenticationPrincipal UserDetails userDetails,
			HttpServletRequest request) {
		
		Member member2 = memberService.getMember(userDetails.getUsername());
		
		// 제목을 클릭할 때 조회수 증가
		service.increaseViewCount(id);
		
		QnaQuestion question = service.getQuestion(id);
		model.addAttribute("question",question);
		model.addAttribute("userGrade", member2.getUserGrade());
		model.addAttribute("currentUrl", request.getRequestURI());
		
		String username = principal.getName();
        List<Member> members = memberService.findByMember(username);

        boolean hasAdminRole = members.stream().anyMatch(member -> member.getUserGrade() == 1);
        Member author = question.getAuthor();
        String authorUsername = author.getUsername();
        if (!hasAdminRole && !username.equals(authorUsername)) {
            return "redirect:/accessDenied";
        }
		
		return "qna/question/question_detail";	
	}
	
	@PreAuthorize("isAuthenticated")
	@GetMapping("/vote/{id}")
	public String questionVote(@PathVariable("id")Integer id, Principal principal) {
		log.info("id:{}",id);
		log.info("principal.getName:{}",principal.getName());
		
		QnaQuestion question = service.getQuestion(id);
		Member member = memberService.getMember(principal.getName());
		
		service.vote(question, member);
		String returnUrl = String.format("redirect:/qna/question/detail/%s", id);
		
		return returnUrl;
	}
	
	@PreAuthorize("isAuthenticated")
	@GetMapping("/delete/{id}")
	public String questionDelete(@PathVariable("id")Integer id, Principal principal) {
		QnaQuestion question = service.getQuestion(id);
		Member member = memberService.getMember(principal.getName());
		if(!question.getAuthor().getUsername().equals(principal.getName()) && member.getUserGrade() != 1) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
		}
		
		service.delete(question);
		
		return "redirect:/qna/question/list";
	}
	
	@GetMapping(value = "/list")
	public String list(Model model,
			@RequestParam(value = "page", defaultValue = "0")int page
			,@RequestParam(value = "keyword", defaultValue = "")String keyword
			,Principal principal
			,HttpServletRequest request) {
		
		
		Page<QnaQuestion> paging = service.getList(page,keyword);
		
		model.addAttribute("paging",paging);
		model.addAttribute("keyword", keyword);
		model.addAttribute("page", "qna");
		model.addAttribute("currentUrl", request.getRequestURI());
		log.info("size:"+paging.getSize());
		
		 // 로그인한 사용자의 userGrade 가져오기
	    if (principal != null) {
	        String username = principal.getName();
	        Member member = memberService.getMember(username); // 유저 정보 가져오기
	        model.addAttribute("userGrade", member.getUserGrade()); // userGrade를 모델에 추가
	    } else {
	        model.addAttribute("userGrade", 0); // 비로그인 사용자는 userGrade = 0
	    }
		
		return "qna/question/question_list";
	}
}
