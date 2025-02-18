package com.pcwk.ehr.member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
	public Specification<Member> search(String keyword){
	    return new Specification<>() {
	        @Override
	        public Predicate toPredicate(Root<Member> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
	            // 검색 조건 정의
	            return criteriaBuilder.or(criteriaBuilder.like(root.get("username"), "%"+keyword+"%"),  // 사용자 이름 검색
	                                      criteriaBuilder.like(root.get("email"), "%"+keyword+"%"),    // 이메일 검색
	                                      criteriaBuilder.like(root.get("userDisName"), "%"+keyword+"%"), // 표시 이름 검색
	                                      criteriaBuilder.like(root.get("userRegDt").as(String.class), "%"+keyword+"%") // 가입일 검색
	            );
	        }
	    };
	}
	
	public Member getMember(String username) {
		Optional<Member> member = memberRepository.findByusername(username);
		
		if(member.isPresent()) {
			return member.get();
		}else {
			throw new DataNotFoundException();
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
		
		log.debug("outUser:{}",outMember);
		return outMember;
	}
	
	public Page<Member> getList(int page, String keyword){
		log.info("┌──────────────────────┐");
		log.info("│ getList()    		 │");
		log.info("└──────────────────────┘");
		
		log.info("param:page{}",page);
		log.info("keyword:{}",keyword);
		
		//날짜 순으로
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("userRegDt"));
		
		Pageable pageable = PageRequest.of(page, 10,Sort.by(sorts));
		
		Specification<Member> specification = search(keyword);
		
		return memberRepository.findAll(specification,pageable);
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
	
	public Member modify(Member member,String username, String password, String email, String userDisName) {
		member.setUsername(username);
		member.setPassword(password);
		member.setEmail(email);
		member.setUserDisName(userDisName);
		
		Member m = memberRepository.save(member);
		log.info("수정된 정보:{}",m);
		return m;
	}
	
	public Member getMember(Long id) {
		log.info("1.param|id:{} ",id);
		
		Optional<Member> member = memberRepository.findById(id);
		if(member.isPresent() == true) {
			return member.get();
		}else {
			throw new RuntimeException("데이터가 없습니다.");
		}
	}
	
	public List<Member> getList(){
		return memberRepository.findAll();
	}
}
