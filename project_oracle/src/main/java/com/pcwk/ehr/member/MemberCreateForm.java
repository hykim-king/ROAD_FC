package com.pcwk.ehr.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberCreateForm {
	
	@Size(min = 3, max = 25)
	@NotEmpty(message = "사용자ID는 필수 입력입니다.")
	private String username;
	
	@Size(min = 4, max = 20)
	@NotEmpty(message = "비밀번호는 필수 입력입니다.")
	private String password1;
	
	@NotEmpty(message = "비밀번호 확인은 필수 입력입니다.")
	private String password2;
	
	@NotEmpty(message = "이메일은 필수 입력입니다.")
	@Email
	private String email;
	
	@Size(min = 2, max = 30)
	@NotEmpty(message = "사용자 이름은 필수 입력입니다.")
	private String userDisName;
	
}
