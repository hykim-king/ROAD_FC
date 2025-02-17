package com.pcwk.ehr.member;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

@RequestMapping("/member")
@Controller
public class MemberController {
	final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	MemberService service;

	public MemberController() {
		log.info("┌──────────────────────┐");
		log.info("│ MemberController()   │");
		log.info("└──────────────────────┘");
	}
	
	@PostMapping("/promote")
	public String promoteMembers(@RequestParam("memberIds") List<Long> memberIds) {
		service.promoteMembersByIds(memberIds);
		return "redirect:/member/list"; // 삭제 후 유저 목록으로 리다이렉트
	}

	@PostMapping("/delete")
	public String deleteMembers(@RequestParam("memberIds") List<Long> memberIds) {
		service.deleteMembersByIds(memberIds);
		return "redirect:/member/list"; // 삭제 후 유저 목록으로 리다이렉트
	}

	@GetMapping("/login")
	public String login() {
		log.info("┌──────────────────────┐");
		log.info("│ login()     		 │");
		log.info("└──────────────────────┘");

		return "member/login_form";
	}

	@GetMapping("/signup")
	public String signup(MemberCreateForm memberCreateForm) {
		log.info("┌──────────────────────┐");
		log.info("│ signup()     		 │");
		log.info("└──────────────────────┘");

		return "member/signup_form";
	}

	@PostMapping("/signup")
	public String signup(@Valid MemberCreateForm memberCreateForm, BindingResult bindingResult) {

		String returnView = "redirect:/member/list";

		if (bindingResult.hasErrors()) {
			return "member/signup_form";
		}

		// 패스워드 일치여부
		if (memberCreateForm.getPassword1().equals(memberCreateForm.getPassword2()) == false) {
			bindingResult.rejectValue("password2", "passwordInCorrect", "패스워드가 일치하지 않습니다.");
		}

		log.info("param username:{}", memberCreateForm.getUsername());
		log.info("param password1:{}", memberCreateForm.getPassword1());
		log.info("param password2:{}", memberCreateForm.getPassword2());
		log.info("param email:{}", memberCreateForm.getEmail());

		try {
			Member user = service.create(memberCreateForm.getUsername(), memberCreateForm.getPassword1(),
					memberCreateForm.getEmail(), memberCreateForm.getUserDisName());
			log.info("user:{}", user);
		} catch (DataIntegrityViolationException e) {
			log.info("DataIntegrityViolationException" + e.getMessage());
			bindingResult.reject("Sign_9899", "이미 등록된 사용자입니다.");

			return "member/signup_form";
		} catch (Exception e) {
			bindingResult.reject("Sign_9899", e.getMessage());

			return "member/signup_form";
		}

		return returnView;
	}

	@GetMapping(value = "/list")
	public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "keyword", defaultValue = "") String keyword) {

		Page<Member> paging = service.getList(page, keyword);

		model.addAttribute("paging", paging);
		model.addAttribute("keyword", keyword);
		log.info("size:" + paging.getSize());

		return "member/member_list";
	}
}
