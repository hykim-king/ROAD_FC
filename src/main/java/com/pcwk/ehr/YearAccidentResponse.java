package com.pcwk.ehr;

import java.time.LocalDateTime;

public class YearAccidentResponse {
    private int tdId;
    private int tdYear;
    private int tdAccident;
    private int tdDeathCnt;
    private int tdInjuryCnt;
    private LocalDateTime tdRegDt;
    private Integer taDeathRate;
    private Integer taInjuryRate;
    private Integer taFatalRate;
    private Integer taAccidentRate;

    // 생성자
    public YearAccidentResponse(int tdId, int tdYear, int tdAccident, int tdDeathCnt, int tdInjuryCnt,
                                LocalDateTime tdRegDt, Integer taDeathRate, Integer taInjuryRate,
                                Integer taFatalRate, Integer taAccidentRate) {
        this.tdId = tdId;
        this.tdYear = tdYear;
        this.tdAccident = tdAccident;
        this.tdDeathCnt = tdDeathCnt;
        this.tdInjuryCnt = tdInjuryCnt;
        this.tdRegDt = tdRegDt;
        this.taDeathRate = taDeathRate;
        this.taInjuryRate = taInjuryRate;
        this.taFatalRate = taFatalRate;
        this.taAccidentRate = taAccidentRate;
    }

    // getter와 setter 메서드 생략
}

