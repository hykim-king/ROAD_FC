package com.pcwk.ehr.qna;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.pcwk.ehr.qna.QnaAnswer;
import com.pcwk.ehr.member.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@Entity
@Table(name = "qna_question")
public class QnaQuestion {

	@Id  //PK
	@GeneratedValue(strategy = GenerationType.IDENTITY) //IDENTITY
	private Integer  id;     
	
	@Column(length = 200) 
	private String   subject;//제목
	
	@Lob
	@Column(columnDefinition = "CLOB") //oracle CLOB
	private String   content;//내용
	
	@Column(length = 1000)
	private String img;  // 이미지 경로 저장
	
	private Integer view_count=0;
	
	private LocalDateTime createDate;//생성일
	//1:N
	// cascade = CascadeType.REMOVE => Oracle On delete cascade : 
	// 부모 데이터 삭제시 관련 자식(Answer)데이터도 같이 삭제.
	@OneToMany(mappedBy ="question", cascade = CascadeType.REMOVE )
	private List<QnaAnswer> answerList;
	
	//한명이 질문을 여러개 작성
	@ManyToOne
	private Member author;
	
	private LocalDateTime modifyDate; //수정일
	
	//하나의 질문에 여러 사람이 추천, 한 사람이 여러 개의 질문 추천
	@ManyToMany
	Set<Member> voter;
}
