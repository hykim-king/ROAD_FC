package com.pcwk.ehr.qna;


import java.time.LocalDateTime;
import java.util.Set;

import com.pcwk.ehr.qna.QnaQuestion;
import com.pcwk.ehr.member.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "qna_answer")
public class QnaAnswer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	
	@Column(columnDefinition = "CLOB") //oracle CLOB
	private String   content;//내용
	
	private LocalDateTime createDate;//생성일
	
	//N:1
	@ManyToOne
	private QnaQuestion question;
	
	@ManyToOne
	private Member author;
	
	private LocalDateTime modifyDate; //수정일
	
	@ManyToMany
	Set<Member> voter;
}
