package com.pcwk.ehr.member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Member{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;      //유저 생성id 값
	
	@Column(unique = true)
	private String username;   //userId
	
	private String password;   //비밀번호
	
	@ColumnDefault(value = "0")
	private int userGrade;   //유저 구분
	
	@Column(unique = true)   // 이메일
	private String email;
	
	private String userDisName;  // 유저 이름
	
	private LocalDateTime userRegDt; // 생성일
	
}