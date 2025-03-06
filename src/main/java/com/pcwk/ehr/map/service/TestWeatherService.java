package com.pcwk.ehr.map.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pcwk.ehr.RegionData;
import com.pcwk.ehr.SafetyIndexDTO;
import com.pcwk.ehr.map.repository.WeatherRepository;
import com.pcwk.ehr.station.StationRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TestWeatherService {
	@Autowired
	WeatherRepository weatherRepository;

	@Autowired
	StationRepository stationRepository;

	public List<SafetyIndexDTO> getAvgSafetyIndex() {
		List<Object[]> weatherStationInfo = weatherRepository.dataNeededForSafetyIndex();
		Map<String, List<Integer>> areaScores = new HashMap<>();

		for (Object[] data : weatherStationInfo) {
			String stationArea = (String) data[8];
			int safetyIndex = calculateIndex(data);
			areaScores.computeIfAbsent(getRegionKey(stationArea), k -> new ArrayList<>()).add(safetyIndex);
		}

		List<SafetyIndexDTO> result = new ArrayList<>();

		for (Map.Entry<String, List<Integer>> entry : areaScores.entrySet()) {
			String region = entry.getKey();
			List<Integer> scores = entry.getValue();
			int avgSafetyIndex = (int) scores.stream().mapToInt(Integer::intValue).average().orElse(0);

			result.add(new SafetyIndexDTO(region, avgSafetyIndex));
		}
		return result;
	}

	public List<SafetyIndexDTO> getAllSafetyIndex() {
		List<Object[]> weatherStationInfo = weatherRepository.dataNeededForSafetyIndex();
		List<SafetyIndexDTO> result = new ArrayList<>();

		for (Object[] data : weatherStationInfo) {
			int stationId = (int) data[0];
			String stationArea = (String) data[8];
			int safetyIndex = calculateIndex(data);
			double stationLat = ((BigDecimal) data[6]).doubleValue();
		    double stationLon = ((BigDecimal) data[7]).doubleValue();
			String region = getRegionKey(stationArea);

			result.add(new SafetyIndexDTO(stationId, region, stationLat, stationLon, safetyIndex));
		}
		return result;
	}

	private String getRegionKey(String area) {
		if (area.contains("강원")) return "gangwon";
		if (area.contains("충청")) return "chungcheong";
		if (area.contains("전라")) return "jeolla";
		if (area.contains("경상")) return "gyeongsang";
		if (area.contains("서울") || area.contains("경기") || area.contains("인천")) return "seoul";
		return "nationwide";
	}

	private int calculateIndex(Object[] data) {
		double temp = ((BigDecimal) data[5]).doubleValue();
		double windSpeed = ((BigDecimal) data[4]).doubleValue();
		double visibility = ((BigDecimal) data[3]).doubleValue();
		double precipitation = ((BigDecimal) data[1]).doubleValue();
		double snowfall = ((BigDecimal) data[2]).doubleValue();

		double dangerScore = 0;

		if (temp < 0) {
			dangerScore += calcWindSpeedScore(windSpeed, 20);
			dangerScore += calcVisibilityDistanceScore(visibility, 20);
			dangerScore += calcPrecipitationScore(precipitation, 30);
			dangerScore += calcSnowfallScore(snowfall, 30);
		} else if (0 <= temp && temp <= 5) {
			dangerScore += calcWindSpeedScore(windSpeed, 30);
			dangerScore += calcVisibilityDistanceScore(visibility, 20);
			dangerScore += calcPrecipitationScore(precipitation, 30);
			dangerScore += calcSnowfallScore(snowfall, 25);
		} else {
			dangerScore += calcWindSpeedScore(windSpeed, 15);
			dangerScore += calcVisibilityDistanceScore(visibility, 30);
			dangerScore += calcPrecipitationScore(precipitation, 55);
		}

		return (int) Math.round(100 - dangerScore);
	}

	private double calcWindSpeedScore(double ws, int weight) {
		double score = 0;
		if (ws == 0 || ws <= 3) {
			score = 0;
		} else if (4 <= ws && ws <= 8) {
			score = weight * 0.2;
		} else if (9 <= ws && ws <= 15) {
			score = weight * 0.4;
		} else if (16 <= ws && ws <= 22) {
			score = weight * 0.6;
		} else if (23 <= ws && ws <= 26) {
			score = weight * 0.8;
		} else if (27 <= ws) {
			score = weight;
		} else { // 0 이하일 땐 안전 처리
			score = 0;
		}

		return score;
	}

	private double calcVisibilityDistanceScore(double vd, int weight) {
		double score = 0;
		if (vd == 0 || vd <= 20) {
			score = weight;
		} else if (21 <= vd && vd <= 50) {
			score = weight * 0.8;
		} else if (51 <= vd && vd <= 100) {
			score = weight * 0.6;
		} else if (101 <= vd && vd <= 300) {
			score = weight * 0.4;
		} else if (301 <= vd && vd <= 500) {
			score = weight * 0.2;
		} else if (501 <= vd) { // 0 이하일 땐 위험 처리
			score = 0;
		} else {
			score = weight;
		}

		return score;
	}

	private double calcSnowfallScore(double sf, int weight) {
		double score = 0;
		if (sf == 0) {
			score = 0;
		} else if (1 <= sf && sf <= 5) {
			score = weight * 0.25;
		} else if (6 <= sf && sf <= 10) {
			score = weight * 0.5;
		} else if (11 <= sf && sf <= 14) {
			score = weight * 0.75;
		} else if (15 <= sf) {
			score = weight;
		} else { // 0 이하일 땐 안전 처리
			score = 0;
		}

		return score;
	}

	private double calcPrecipitationScore(double pre, int weight) {
		double score = 0;
		if (pre == 0) {
			score = 0;
		} else if (1 <= pre && pre <= 7) {
			score = weight * 0.2;
		} else if (8 <= pre && pre <= 16) {
			score = weight * 0.4;
		} else if (17 <= pre && pre <= 24) {
			score = weight * 0.6;
		} else if (25 <= pre && pre <= 29) {
			score = weight * 0.8;
		} else if (30 <= pre) {
			score = weight;
		} else { // 0 이하일 땐 안전 처리
			score = 0;
		}

		return 0;
	}
}