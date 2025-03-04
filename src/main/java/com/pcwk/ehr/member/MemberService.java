package com.pcwk.ehr.member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.pcwk.ehr.DataNotFoundException;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberService {
	Logger log = LoggerFactory.getLogger(getClass());

	final MemberRepository memberRepository;
	final PasswordEncoder passwordEncoder;

	public Specification<Member> search(String keyword) {
		return new Specification<>() {
			@Override
			public Predicate toPredicate(Root<Member> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				// 검색 조건 정의
				return criteriaBuilder.or(criteriaBuilder.like(root.get("username"), "%" + keyword + "%"), // 사용자 이름 검색
						criteriaBuilder.like(root.get("email"), "%" + keyword + "%"), // 이메일 검색
						criteriaBuilder.like(root.get("userDisName"), "%" + keyword + "%"), // 표시 이름 검색
						criteriaBuilder.like(root.get("userRegDt").as(String.class), "%" + keyword + "%") // 가입일 검색
				);
			}
		};
	}

	public Optional<Member> findByEmail(String email) {
		return memberRepository.findByEmail(email);
	}

	public String generateTemporaryPassword(Member member) {
		String tempPassword = generateRandomPassword();
		// 임시 비밀번호를 사용하여 member 엔티티 업데이트
		member.setPassword(tempPassword); // 실제로는 암호화하여 저장
		memberRepository.save(member);
		// 이메일로 임시 비밀번호 발송 (여기에서는 생략, 실제 구현 시 이메일 발송 코드 추가)
		return tempPassword;
	}

	private String generateRandomPassword() {
		int length = 10;
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuilder password = new StringBuilder();
		for (int i = 0; i < length; i++) {
			password.append(characters.charAt(random.nextInt(characters.length())));
		}
		return password.toString();
	}

	public void updatePassword(String username, String newPassword) {
		Optional<Member> member = memberRepository.findByusername(username);
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		member.ifPresent(value -> {
			value.setPassword(passwordEncoder.encode(newPassword)); // 실제로는 암호화하여 저장
			memberRepository.save(value);
		});
	}
	
	//principal의 아이디와 비밀번호를 입력받아 일치하는 계정 찾기
    public boolean checkCurrentPassword(String currentPassword) {
        // 현재 로그인된 사용자 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Member> optionalUser = memberRepository.findByusername(username);

        if (!optionalUser.isPresent()) {
            throw new UsernameNotFoundException("User not found");
        }

        // 비밀번호 비교
        Member user = optionalUser.get();
        return passwordEncoder.matches(currentPassword, user.getPassword());
    }

	public boolean checkUsernameExists(String username) {
		try {
			Optional<Member> member = memberRepository.findByusername(username);
			return member.isPresent();
		} catch (Exception e) {
			// 로그 출력
			System.err.println("Error checking username: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public Optional<Member> findMember(String username) {
		return memberRepository.findByusername(username);
	}

	public List<Member> findByMember(String username) {
		return memberRepository.findByUsername(username);
	}

	public Member getMember(String username) {
		Optional<Member> member = memberRepository.findByusername(username);

		if (member.isPresent()) {
			return member.get();
		} else {
			throw new DataNotFoundException();
		}
	}
	
	public Member changePassword(String username, MemberChPass pass, BindingResult bindingResult) {
        Optional<Member> optionalMember = memberRepository.findByusername(username);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            
            // 현재 비밀번호 확인
            if (!passwordEncoder.matches(pass.getPassword(), member.getPassword())) {
                bindingResult.rejectValue("password", "passwordInCorrect", "현재 비밀번호가 일치하지 않습니다.");
                return null;
            }

            // 새 비밀번호 확인
            if (!pass.getPassword1().equals(pass.getPassword2())) {
                bindingResult.rejectValue("password2", "passwordInCorrect", "새 비밀번호가 일치하지 않습니다.");
                return null;
            }

            // 새 비밀번호 암호화 후 저장
            member.setPassword(passwordEncoder.encode(pass.getPassword1()));
            memberRepository.save(member);
            return member;
        } else {
            bindingResult.reject("userNotFound", "사용자를 찾을 수 없습니다.");
            return null;
        }
	}
    


	public Member create(String username, String password, String email, String userDisName) {
		Member member = new Member();
		member.setUsername(username);
		member.setPassword(password);
		member.setEmail(email);
		member.setUserDisName(userDisName);
		member.setUserRegDt(LocalDateTime.now());

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		member.setPassword(passwordEncoder.encode(password));

		Member outMember = memberRepository.save(member);

		log.debug("outUser:{}", outMember);
		return outMember;
	}

	public Page<Member> getList(int page, String keyword) {
		log.info("┌──────────────────────┐");
		log.info("│ getList()    		 │");
		log.info("└──────────────────────┘");

		log.info("param:page{}", page);
		log.info("keyword:{}", keyword);

		// 날짜 순으로
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("userRegDt"));

		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));

		Specification<Member> specification = search(keyword);

		return memberRepository.findAll(specification, pageable);
	}

	@Transactional
	public void promoteMembersByIds(List<Long> ids) {
		memberRepository.promoteToAdmin(ids);
	}

	@Transactional
	public void deleteMembersByIds(List<Long> ids) {
		memberRepository.deleteByIdsInQuery(ids);
	}

	public void delete(Member member) {
		memberRepository.delete(member);
	}

	public Member modify(Member member, String username, String password, String email, String userDisName) {
		member.setUsername(username);
		member.setPassword(password);
		member.setEmail(email);
		member.setUserDisName(userDisName);

		Member m = memberRepository.save(member);
		log.info("수정된 정보:{}", m);
		return m;
	}

	public Member getMember(Long id) {
		log.info("1.param|id:{} ", id);

		Optional<Member> member = memberRepository.findById(id);
		if (member.isPresent() == true) {
			return member.get();
		} else {
			throw new RuntimeException("데이터가 없습니다.");
		}
	}

	public List<Member> getList() {
		return memberRepository.findAll();
	}
}
