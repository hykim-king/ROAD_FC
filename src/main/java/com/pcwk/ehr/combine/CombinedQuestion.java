package com.pcwk.ehr.combine;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CombinedQuestion {
    private Integer id;
    private String subject;
    private String content;
    private String type; // "report" 또는 "qna"
    // getters and setters
}


