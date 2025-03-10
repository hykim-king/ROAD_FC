package com.pcwk.ehr.member;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pcwk.ehr.combine.CombinedQuestion;
import com.pcwk.ehr.combine.CombinedService;
import com.pcwk.ehr.email.MailSendService;
import com.pcwk.ehr.qna.QnaQuestion;
import com.pcwk.ehr.qna.QnaQuestionService;
import com.pcwk.ehr.report.ReportAnswer;
import com.pcwk.ehr.report.ReportAnswerService;
import com.pcwk.ehr.report.ReportQuestion;
import com.pcwk.ehr.report.ReportQuestionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RequestMapping("/member")
@Controller
public class MemberController {
	final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	MemberService service;

	@Autowired
	MailSendService mailSendService; // 이메일 전송 서비스 주입

	@Autowired
	CombinedService combinedService;

	@Autowired
	ReportAnswerService answerService;

	public MemberController() {
		log.info("┌──────────────────────┐");
		log.info("│ MemberController()   │");
		log.info("└──────────────────────┘");
	}

	@GetMapping("/myanswer")
	public String getMyAnswer(@AuthenticationPrincipal UserDetails user, Model model,
			@RequestParam(value = "page", defaultValue = "0") int page, HttpServletRequest request) {

		Member member = service.getMember(user.getUsername());
		Page<ReportAnswer> paging = answerService.getQuestionsByAuthor(member, page);

		model.addAttribute("paging", paging);
		model.addAttribute("member", member);
		model.addAttribute("page", "myanswer");
		model.addAttribute("currentUrl", request.getRequestURI());

		return "member/my_answer";
	}

	@GetMapping("/myquestion")
	public String getMyQuestions(@AuthenticationPrincipal UserDetails user, Model model,
			@RequestParam(value = "page", defaultValue = "0") int page, HttpServletRequest request) {
		Member member = service.getMember(user.getUsername());

		// 사용자가 작성한 ReportQuestions와 QnaQuestions를 합쳐서 조회
		Page<CombinedQuestion> paging = combinedService.getCombinedQuestions(member, page);

		model.addAttribute("paging", paging);
		model.addAttribute("member", member);
		model.addAttribute("page", "myquestion");
		model.addAttribute("currentUrl", request.getRequestURI());

		return "member/my_question";
	}

	@GetMapping("/profile")
	public String profile(Model model, @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
		log.info("┌──────────────────────┐");
		log.info("│ profile()   	 	  │");
		log.info("└──────────────────────┘");
		if (userDetails == null) {
			return "redirect:/member/login";
		}

		Member member = service.getMember(userDetails.getUsername());
		model.addAttribute("member", member);
		model.addAttribute("page", "profile");
		model.addAttribute("currentUrl", request.getRequestURI());

		return "member/member_profile";
	}

	@PostMapping("/withdraw")
	public String withdraw(@AuthenticationPrincipal UserDetails userDetails) {
		Member user = service.getMember(userDetails.getUsername());
		service.deleteMember(user.getId());
		service.delete(user);

		return "redirect:/member/login";
	}

	@GetMapping("/change")
	public String change(Model model, @AuthenticationPrincipal UserDetails userDetails, MemberChPass pass,
			HttpServletRequest request) {
		if (userDetails == null) {
			return "redirect:/member/login";
		}

		Member member = service.getMember(userDetails.getUsername());
		model.addAttribute("member", member);
		model.addAttribute("memberChPass", new MemberChPass());
		model.addAttribute("page", "change");
		model.addAttribute("currentUrl", request.getRequestURI());

		return "member/change_pass";
	}

	@PostMapping("/change")
	public String change(@AuthenticationPrincipal UserDetails userDetails, MemberChPass pass,
			BindingResult bindingResult) {
		if (userDetails == null) {
			return "redirect:/member/login";
		}

		if (bindingResult.hasErrors()) {
			return "member/change_pass";
		}

		Member member = service.changePassword(userDetails.getUsername(), pass, bindingResult);
		if (member == null) {
			return "member/change_pass";
		}

		return "redirect:/member/profile";
	}

	@GetMapping("/login")
	public String login(Model model, HttpServletRequest request) {
		log.info("┌──────────────────────┐");
		log.info("│ login()              │");
		log.info("└──────────────────────┘");

		model.addAttribute("currentUrl", request.getRequestURI());
		return "member/login_form";
	}

	@GetMapping("/signup")
	public String signup(MemberCreateForm memberCreateForm, Model model, HttpServletRequest request) {
		log.info("┌──────────────────────┐");
		log.info("│ signup()             │");
		log.info("└──────────────────────┘");

		model.addAttribute("currentUrl", request.getRequestURI());
		return "member/signup_form";
	}

	@PostMapping("/signup")
	public String signup(@Valid MemberCreateForm memberCreateForm, BindingResult bindingResult) {
		String returnView = "redirect:/member/login";

		if (bindingResult.hasErrors()) {
			return "member/signup_form";
		}

		if (!memberCreateForm.getPassword1().equals(memberCreateForm.getPassword2())) {
			bindingResult.rejectValue("password2", "passwordInCorrect", "패스워드가 일치하지 않습니다.");
			return "member/signup_form";
		}

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

	@PostMapping("/checkUsername")
	@ResponseBody
	public Map<String, Boolean> checkUsername(@RequestBody Map<String, String> request) {
		String username = request.get("username");
		System.out.println("Received username: " + username); // 디버깅 로그 추가
		boolean exists = service.checkUsernameExists(username);
		return Collections.singletonMap("exists", exists);
	}

	@GetMapping("/findUsername")
	public String findUsername(Model model, HttpServletRequest request) {
		log.info("┌──────────────────────┐");
		log.info("│ findUsername()       │");
		log.info("└──────────────────────┘");
		
		model.addAttribute("currentUrl", request.getRequestURI());
		return "member/find_username";
	}

	@PostMapping("/findUsername")
	@ResponseBody
	public Map<String, String> findUsername(@RequestParam Map<String, String> request) {
		String email = request.get("email");
		Optional<Member> member = service.findByEmail(email);
		return member.map(value -> Collections.singletonMap("username", value.getUsername()))
				.orElseGet(() -> Collections.singletonMap("error", "이메일에 해당하는 아이디가 없습니다."));
	}

	@GetMapping("/findPassword")
	public String checkUsername(Model model, HttpServletRequest request) {
		log.info("┌──────────────────────┐");
		log.info("│ findPassword()       │");
		log.info("└──────────────────────┘");
		
		model.addAttribute("currentUrl", request.getRequestURI());
		return "member/find_password";
	}

	@PostMapping("/findPassword")
	@ResponseBody
	public Map<String, String> findPassword(@RequestParam Map<String, String> request) {
		String email = request.get("email");
		Optional<Member> member = service.findByEmail(email);
		return member.map(value -> {
			// 임시 비밀번호 생성 및 설정
			String tempPassword = service.generateTemporaryPassword(value);
			service.updatePassword(value.getUsername(), tempPassword);
			// 임시 비밀번호를 이메일로 전송
			mailSendService.mailSend("1026rjsdnd@naver.com", email, "임시 비밀번호 발송", "임시 비밀번호는 " + tempPassword + "입니다.");
			return Collections.singletonMap("message", "임시 비밀번호가 이메일로 발송되었습니다.");
		}).orElseGet(() -> Collections.singletonMap("error", "이메일에 해당하는 아이디가 없습니다."));
	}

	@PostMapping("/promote")
	public String promoteMembers(@RequestParam(value = "memberIds", required = false) List<Long> memberIds) {
		if (memberIds == null || memberIds.isEmpty()) {
			return "redirect:/member/list";
		}
		service.promoteMembersByIds(memberIds);
		return "redirect:/member/list";
	}

	@PostMapping("/delete")
	public String deleteMembers(@RequestParam(value = "memberIds", required = false) List<Long> memberIds) {
		if (memberIds == null || memberIds.isEmpty()) {
			return "redirect:/member/list";
		}
		service.deleteMembers(memberIds);
		service.deleteMembersByIds(memberIds);
		return "redirect:/member/list";
	}

	@GetMapping(value = "/list")
	public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "keyword", defaultValue = "") String keyword, Principal principal,
			HttpServletRequest request) {

		if (principal == null) {
			return "redirect:/member/login";
		}

		String username = principal.getName();
		List<Member> members = service.findByMember(username);
		Member currentUser = service.getMember(username);

		boolean hasAdminRole = members.stream().anyMatch(member -> member.getUserGrade() == 1);
		if (!hasAdminRole) {
			return "redirect:/accessDenied";
		}

		Page<Member> paging = service.getList(page, keyword);

		Member member = service.getMember(username); // member 객체 가져오기
		model.addAttribute("member", member); // 모델에 member 추가

		model.addAttribute("currentUser", currentUser);
		model.addAttribute("paging", paging);
		model.addAttribute("keyword", keyword);
		model.addAttribute("page", "manage");
		model.addAttribute("currentUrl", request.getRequestURI());
		log.info("size:" + paging.getSize());

		return "member/member_list";
	}
}