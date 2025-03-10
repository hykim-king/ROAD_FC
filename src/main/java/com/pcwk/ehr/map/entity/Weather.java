package com.pcwk.ehr.map.entity;

import java.time.LocalDateTime;

import com.pcwk.ehr.station.Station;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@Entity
@ToString
public class Weather {

	@Id
	@Column(name="weather_station_id")
	private Integer weather_station_id;				//지점번호
	
	@Column(name="weather_observation_time")
	private String weather_observation_time;			//관측시간
	
	@Column(name="weather_wind_speed", columnDefinition="NUMBER")
	private double weather_wind_speed;				//풍속
	
	@Column(name="weather_temperature", columnDefinition="NUMBER")
	private double weather_temperature;				//기온
	
	@Column(name="weather_humidity", columnDefinition="NUMBER")
	private double weather_humidity;				//습도
	
	@Column(name="weather_precipitation", columnDefinition="NUMBER")
	private double weather_precipitation;			//강수량
	
	@Column(name="weather_snowfall", columnDefinition="NUMBER")
	private double weather_snowfall;				//적설량
	
	@Column(name="weather_cloud_amount", columnDefinition="NUMBER")
	private Integer weather_cloud_amount;			//전운량
	
	@Column(name="weather_visibility_distance", columnDefinition="NUMBER")
	private Integer weather_visibility_distance;	//시정(가시거리)

	@Column(name="weather_ground_temperature", columnDefinition="NUMBER")
	private double weather_ground_temperature;		//지면온도
	
	//weather_station_id를 station이랑 엮을 FK로 설정, referencedColumnName의 station_id는 Station 엔티티의 PK
	@OneToOne
	@JoinColumn(name="weather_station_id", referencedColumnName ="station_id")
	private Station station;
}