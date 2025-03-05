package com.pcwk.ehr.station;



import com.pcwk.ehr.map.entity.Weather;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
public class Station {

	@Id
	@Column(name="station_id")
	private Integer stationId;
	
	@Column(name="station_latitude", columnDefinition = "NUMBER")
	private Double stationLat;
	
	@Column(name="station_longitude", columnDefinition = "NUMBER")
	private Double stationLong;
	
	@Column(name="station_area")
	private String stationArea;
	
	//(mappedBy)Weather 엔티티에서 weather_station_id를 FK로 설정했으므로, WEATHER가 연관관계의 갑.
	//Station을 삭제하면 id로 묶여있는 weather 데이터도 함께 삭제
	//lazy값을 주어서 station을 조회하거나 뿌릴 때 weather값을 뿌리지 않게. .getWeather()로 Weather데이터를ㄹ 가져옴
	@OneToOne(mappedBy ="station", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	private Weather weather;
}