package com.pcwk.ehr.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class YearAccidentRateDTO {
    private final Integer tdYear;
    private final Integer tdAccident;
    private final Integer tdDeathCnt;
    private final Integer tdInjuryCnt;
    private final Integer taAccidentRate;
    private final Integer taDeathRate;
    private final Integer taFatalRate;
    private final Integer taInjuryRate;

    // JPQL 쿼리를 위한 생성자
    public YearAccidentRateDTO(Integer tdYear, Integer tdAccident, Integer tdDeathCnt, Integer tdInjuryCnt, Integer taAccidentRate, Integer taDeathRate, Integer taFatalRate, Integer taInjuryRate) {
        this.tdYear = tdYear;
        this.tdAccident = tdAccident;
        this.tdDeathCnt = tdDeathCnt;
        this.tdInjuryCnt = tdInjuryCnt;
        this.taAccidentRate = taAccidentRate;
        this.taDeathRate = taDeathRate;
        this.taFatalRate = taFatalRate;
        this.taInjuryRate = taInjuryRate;
    }
}
