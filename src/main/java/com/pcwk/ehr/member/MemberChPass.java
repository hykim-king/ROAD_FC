package com.pcwk.ehr.member;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberChPass {
	
	@Size(min = 4, max = 20)
	@NotEmpty(message = "비밀번호는 필수 입력입니다.")
	private String password;
	
	@Size(min = 4, max = 20)
	@NotEmpty(message = "비밀번호는 필수 입력입니다.")
	private String password1;
	
	@NotEmpty(message = "비밀번호 확인은 필수 입력입니다.")
	private String password2;
}