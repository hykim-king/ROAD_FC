package com.pcwk.ehr.data;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "WEATHER_ACCIDENT")
public class WeatherAccident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//기본키생성을 DB에 위임한다.
    @Column(name = "WA_WEATHER_ID")
    private Long waId;
    
    @Column(name = "WA_TRAFFIC_YEAR")
    private Integer waYear;
    
    @Column(name = "WA_ROAD_SHAPE")
    private String waRoadType;
    
    @Column(name = "WA_ACCIDENT_TYPE")
    private String waAccidentType;
    
    @Column(name = "WA_TOTAL_CNT", columnDefinition = "NUMBER")
    private Integer waTotalCnt;
    
    @Column(name = "WA_CLEAR_ACCIDENT", columnDefinition = "NUMBER")
    private Integer waClearCnt;
    
    @Column(name = "WA_CLOUDY_ACCIDENT", columnDefinition = "NUMBER")
    private Integer waCloudyCnt;
    
    @Column(name = "WA_RAINY_ACCIDENT", columnDefinition = "NUMBER")
    private Integer waRainyCnt;
    
    @Column(name = "WA_FOGGY_ACCIDENT", columnDefinition = "NUMBER")
    private Integer waFoggyCnt;
    
    @Column(name = "WA_SNOWY_ACCIDENT", columnDefinition = "NUMBER")
    private Integer waSnowyCnt;
    
    @Column(name = "WA_OTHER_ACCIDENT",columnDefinition = "NUMBER")
    private Integer waOtherCnt;
    
    @Column(name = "WA_REG_DT",columnDefinition = "DATE")
    private LocalDateTime waRegDt;
}
