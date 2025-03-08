package com.pcwk.ehr.member;

import lombok.Getter;

@Getter
public enum MemberRole {
	ADMIN("ROLE_ADMIN"),
	MEMBER("ROLE_MEMBER");
	
	private String value;
	
	MemberRole(String value){
		this.value = value;
	}
}