package com.pcwk.ehr.member;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
public class MemberSecurityService implements UserDetailsService {
	Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	MemberRepository memberRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("1.username:{}",username);
		Optional<Member> _siteUser = memberRepository.findByusername(username);
		
		log.info("2._siteUser.isEmpty():{}",_siteUser.isEmpty());
		if(_siteUser.isEmpty() == true) {
			log.info("2.1._siteUser.isEmpty():{}",_siteUser.isEmpty());
			throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
		}
		
		Member member = _siteUser.get();
		log.info("3.siteUser:{}",member);
		List<GrantedAuthority> authorities = new ArrayList<>();
		
		if("admin".equalsIgnoreCase(username)) {
			log.info("4.UserRole.ADMIN.getValue():{}",MemberRole.ADMIN.getValue());
			authorities.add(new SimpleGrantedAuthority(MemberRole.ADMIN.getValue()));
		}else {
			log.info("4.UserRole.USER.getValue():{}",MemberRole.MEMBER.getValue());
			authorities.add(new SimpleGrantedAuthority(MemberRole.MEMBER.getValue()));
		}

		User user = new User(member.getUsername(), member.getPassword(), authorities);
		log.info("5.user:{}",user);
		
		return user;
	}

}