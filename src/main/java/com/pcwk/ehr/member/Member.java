package com.pcwk.ehr.member;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;

import com.pcwk.ehr.qna.QnaQuestion;
import com.pcwk.ehr.report.ReportQuestion;
import com.pcwk.ehr.faq.FaqQuestion;
import com.pcwk.ehr.notice.NoticeQuestion;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Member {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @ColumnDefault(value = "0")
    private int userGrade;

    @Column(unique = true)
    private String email;

    private String userDisName;

    private LocalDateTime userRegDt;
}
