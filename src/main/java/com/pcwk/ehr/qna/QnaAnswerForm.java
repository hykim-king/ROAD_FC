package com.pcwk.ehr.qna;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Setter
@Getter
@ToString
@Slf4j
public class QnaAnswerForm {
	
	@NotEmpty(message = "내용은 필수 입력입니다.")
	private String content;
	
}
