package com.pcwk.ehr.map.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pcwk.ehr.map.entity.Weather;
import com.pcwk.ehr.map.repository.WeatherRepository;

@Service
public class WeatherService {
	@Autowired
	WeatherRepository weatherRepository;

	public List<Weather> getSafetyIndex() {
		List<Weather> weatherList = weatherRepository.getWeatherInfo();
		
        // 안전 지수 계산 후 Weather 객체에 설정
        weatherList.forEach(weather -> {
            weather.setSafetyIndex(calculateSafetyIndex(weather));
        });

        return weatherList;
	}
	
    private int calculateSafetyIndex(Weather weather) {
        double score = 0;

        double temperature = weather.getWeather_temperature();
        double precipitation = weather.getWeather_precipitation();
        double snowfall = weather.getWeather_snowfall();
        double visibility = weather.getWeather_visibility_distance();
        double windSpeed = weather.getWeather_wind_speed();

        if (temperature < 0) {
            score += getScore(precipitation, new double[]{30, 21, 15, 6, 1, 0}, new int[]{5, 10, 15, 20, 25, 30}, 30);
            score += getScore(snowfall, new double[]{15, 11, 7, 4, 1, 0}, new int[]{5, 10, 15, 20, 25, 30}, 30);
            score += getScore(visibility, new double[]{0, 21, 51, 101, 301}, new int[]{0, 5, 10, 15, 20}, 20);
            score += getScore(windSpeed, new double[]{27, 21, 14, 9, 5, 0}, new int[]{0, 4, 8, 12, 16, 20}, 20);
        } else if (temperature <= 5) {
            score += getScore(precipitation, new double[]{30, 21, 15, 6, 1, 0}, new int[]{5, 10, 15, 20, 25, 30}, 30);
            score += getScore(snowfall, new double[]{15, 11, 7, 4, 1, 0}, new int[]{0, 5, 10, 15, 20, 25}, 25);
            score += getScore(visibility, new double[]{0, 21, 51, 101, 301}, new int[]{0, 5, 10, 15, 20}, 20);
            score += getScore(windSpeed, new double[]{27, 21, 14, 9, 5, 0}, new int[]{5, 10, 15, 20, 25, 30}, 30);
        } else {
            score += getScore(precipitation, new double[]{30, 21, 15, 6, 1, 0}, new int[]{5, 10, 20, 30, 40, 55}, 55);
            score += getScore(visibility, new double[]{0, 21, 51, 101, 301}, new int[]{0, 5, 10, 15, 20}, 30);
            score += getScore(windSpeed, new double[]{27, 21, 14, 9, 5, 0}, new int[]{0, 3, 6, 9, 12, 15}, 15);
        }
        
        System.out.println("Total safety index: " + score);
        return (int) Math.min(score, 100); // 최대 100점 제한
    }

    private double getScore(double value, double[] thresholds, int[] scores, double weight) {
        for (int i = 0; i < thresholds.length; i++) {
            if (value >= thresholds[i]) {
                return scores[i];
            }
        }
        return 0;
    }
}