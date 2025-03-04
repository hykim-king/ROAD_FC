package com.pcwk.ehr.qna;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QnaQuestionForm {
	
	@NotEmpty(message = "제목은 필수 입력입니다.")
	@Size(max = 200)
	private String subject;
	
	@NotEmpty(message = "내용은 필수 입력입니다.")
	private String content;
	
	private MultipartFile[] imgFile;  // 업로드된 이미지 파일
}
