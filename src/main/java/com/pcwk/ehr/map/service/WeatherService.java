package com.pcwk.ehr.map.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pcwk.ehr.map.entity.Weather;
import com.pcwk.ehr.map.repository.WeatherRepository;
import com.pcwk.ehr.station.Station;
import com.pcwk.ehr.station.StationRepository;

@Service
public class WeatherService {
	
	@Autowired
	WeatherRepository weatherRepository;

	@Autowired
	StationRepository stationRepository;

	public List<Weather> weatherList() {
		return weatherRepository.findAll();
	}

	public List<Station> stationList() {
		return stationRepository.findAll();
	}

	public List<Object[]> weatherStationList() {
		return weatherRepository.dataNeededForSafetyIndex();
	}
	
//	for(Object[] data: weatherStationInfo) {
//	System.out.println("Station Id: " + data[0]);
//	System.out.println("Precipitation: " + data[1]);
//	System.out.println("Snowfall: " + data[2]);
//	System.out.println("Visibility Distance: " + data[3]);
//	System.out.println("Wind speed: " + data[4]);
//	System.out.println("Temperature: " + data[5]);
//	System.out.println("Station Latitude: " + data[6]);
//	System.out.println("Station Longitude: " + data[7]);
//}

	public void getSafetyIndex() {
	    List<Object[]> weatherStationInfo = weatherRepository.dataNeededForSafetyIndex();
	    
	    double temp = 0;

	    for (Object[] data : weatherStationInfo) {
	    	 double dangerScore = 0;
	        // BigDecimal로 캐스팅 후 doubleValue()로 변환
	        temp = ((BigDecimal) data[5]).doubleValue();
	        System.out.print(data[0]);
	        if (temp < 0) { // 영하일 때
	            dangerScore += calcWindSpeedScore(((BigDecimal) data[4]).doubleValue(), 20);
	            dangerScore += calcVisibilityDistanceScore(((BigDecimal) data[3]).doubleValue(), 20);
	            dangerScore += calcPrecipitationScore(((BigDecimal) data[1]).doubleValue(), 30);
	            dangerScore += calcSnowfallScore(((BigDecimal) data[2]).doubleValue(), 30);
	            
	            System.out.println("안전지수: " + (int) Math.round(100 - dangerScore));
	            //return (int) Math.round(100 - dangerScore);
	        } else if (0 <= temp && temp <= 5) { // 0 ~ 5도일 때
	            dangerScore += calcWindSpeedScore(((BigDecimal) data[4]).doubleValue(), 15);
	            dangerScore += calcVisibilityDistanceScore(((BigDecimal) data[3]).doubleValue(), 30);
	            dangerScore += calcPrecipitationScore(((BigDecimal) data[1]).doubleValue(), 30);
	            dangerScore += calcSnowfallScore(((BigDecimal) data[2]).doubleValue(), 25);
	            
	            System.out.println("안전지수: " + (int) Math.round(100 - dangerScore));
	            //return (int) Math.round(100 - dangerScore);
	        } else if (5 < temp) { // 6도 이상일 때  
	            dangerScore += calcWindSpeedScore(((BigDecimal) data[4]).doubleValue(), 15);
	            dangerScore += calcVisibilityDistanceScore(((BigDecimal) data[3]).doubleValue(), 30);
	            dangerScore += calcPrecipitationScore(((BigDecimal) data[1]).doubleValue(), 55);
	            // dangerScore += calcSnowfallScore(((BigDecimal) data[2]).doubleValue(), 0);
	            
	            System.out.println("안전지수: " + (int) Math.round(100 - dangerScore));
	            //return (int) Math.round(100 - dangerScore);
	        } else {
	            System.out.println("SOMETHING'S WRONG WITH YOUR TEMPERATURE");	            
	        }
	    }
	}
	
	private double calcWindSpeedScore(double ws, int weight) {
		double score = 0;
		if(ws == 0 || ws <= 3) {
			score = 0;
		} else if(4<=ws && ws <= 8) {
			score = weight * 0.2;
		} else if(9<=ws && ws <= 15) {
			score = weight * 0.4;
		} else if(16<=ws && ws <= 22) {
			score = weight * 0.6;
		} else if(23<=ws && ws <= 26) {
			score = weight * 0.8;
		} else if(27 <= ws) {
			score = weight;
		} else { //  0 이하일 땐 안전 처리
			score = 0;
		}
			 
		return score;
	}
	
	private double calcVisibilityDistanceScore(double vd, int weight) {
		double score = 0;
		if(vd == 0 || vd <= 20) {
			score = weight;
		} else if(21 <= vd && vd <= 50) {
			score = weight * 0.8;
		} else if(51 <= vd && vd <= 100) {
			score = weight * 0.6;
		} else if(101 <= vd && vd <= 300) {
			score = weight * 0.4;
		} else if(301 <= vd && vd <= 500) {
			score = weight * 0.2;
		} else if(501 <= vd){ // 0 이하일 땐 위험 처리
			score = 0;
		} else {
			score = weight;
		}
		
		return score;
	}
	
	private double calcSnowfallScore(double sf, int weight) {
		double score = 0;
		if(sf == 0) {
			score = 0;
		} else if(1 <= sf && sf <= 5) {
			score = weight * 0.25;
		} else if(6 <= sf && sf <= 10) {
			score = weight * 0.5;
		} else if(11 <= sf && sf <= 14) {
			score = weight * 0.75;
		} else if(15 <= sf) {
			score = weight;
		} else { // 0 이하일 땐 안전 처리
			score = 0;
		}
					
		return score;
	}
	
	private double calcPrecipitationScore(double pre, int weight) {
		double score = 0;
		if(pre == 0) {
			score = 0;
		} else if(1 <= pre && pre <= 7) {
			score = weight * 0.2;
		} else if(8 <= pre && pre <= 16) {
			score = weight * 0.4;
		} else if(17 <= pre && pre <= 24) {
			score = weight * 0.6;
		} else if(25 <= pre && pre <= 29) {
			score = weight * 0.8;
		} else if(30 <= pre) {
			score = weight;
		} else { // 0 이하일 땐 안전 처리
			score = 0;
		}
		
		return 0;
	}
}