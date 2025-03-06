//
//package com.pcwk.ehr.DataController;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.pcwk.ehr.DataService.LocalAccidentService;
//import com.pcwk.ehr.DataService.TrafficAccidentService;
//import com.pcwk.ehr.DataService.TrafficComparisonService;
//import com.pcwk.ehr.DataService.WeatherAccidentService;
//import com.pcwk.ehr.DataService.YearAccidentRateService;
//import com.pcwk.ehr.DataService.YearAccidentService;
//import com.pcwk.ehr.data.LocalAccident;
//
//@Controller
//@RequestMapping("/DataChart")
//public class LocalAccidentController {
//
//	final Logger log = LoggerFactory.getLogger(getClass());
//	
//	@Autowired
//	LocalAccidentService localAccidentService;
//	
//	@Autowired
//	TrafficAccidentService trafficAccidentService;
//	
//	@Autowired
//	TrafficComparisonService trafficComparisonService;
//	
//	@Autowired
//	WeatherAccidentService weatherAccidentService;
//	
//	@Autowired
//	YearAccidentRateService yearAccidentRateService;
//	
//	@Autowired
//	YearAccidentService yearAccidentService;
//
//	@GetMapping("/list")
//	public String listAllAccidents(@RequestParam(value = "year", required = false) Integer year,
//			@RequestParam(value = "majorRegion", required = false) String majorRegion,
//			@RequestParam(value = "minorRegion", required = false) String minorRegion,
//			@RequestParam(value = "keyword", required = false) String keyword,
//			@RequestParam(value = "page", defaultValue = "0") int page,
//			@RequestParam(value = "size", defaultValue = "10") int size, Model model) throws JsonProcessingException {
//
//		Pageable pageable = PageRequest.of(page, size);
//		log.info("pageable:{}", pageable);
//		Page<LocalAccident> pagedAccidents;
//
//		if (keyword != null && !keyword.isEmpty()) {
//			pagedAccidents = localAccidentService.getPagedLocalAccidents(keyword, page, size);
//		} else if (year != null || (majorRegion != null && !majorRegion.isEmpty())
//				|| (minorRegion != null && !minorRegion.isEmpty())) {
//			pagedAccidents = localAccidentService.getAccidentsFilteredPaged(year, majorRegion, minorRegion, pageable);
//		} else {
//			pagedAccidents = localAccidentService.getAllLocalAccidentsPaged(pageable);
//		}
//
//		List<Integer> yearsList = localAccidentService.getAllYears();
//		List<String> majorRegions = localAccidentService.getAllMajorRegions();
//		List<String> minorRegions = localAccidentService.getAllMinorRegions();
//		List<Object[]> yearlyAccidentData = localAccidentService.getYearlyAccidentCount();
//
//		// 변환 로직 추가
//		List<Map<String, Object>> transformedData = new ArrayList<>();
//		for (Object[] data : yearlyAccidentData) {
//			Map<String, Object> dataMap = new HashMap<>();
//			dataMap.put("year", data[0]);
//			dataMap.put("accidentCount", data[1]);
//			transformedData.add(dataMap);
//		}
//
//		// 변환된 데이터를 JSON으로 직렬화하여 반환
//		String jsonData = new ObjectMapper().writeValueAsString(transformedData);
//
//		model.addAttribute("accidents", pagedAccidents.getContent());
//		model.addAttribute("paging", pagedAccidents);
//		model.addAttribute("years", localAccidentService.getAllYears());
//		model.addAttribute("majorRegions", localAccidentService.getAllMajorRegions());
//		model.addAttribute("minorRegions", localAccidentService.getAllMinorRegions());
//		model.addAttribute("selectedYear", year);
//		model.addAttribute("selectedMajorRegion", majorRegion);
//		model.addAttribute("selectedMinorRegion", minorRegion);
//		model.addAttribute("keyword", keyword);
//		model.addAttribute("yearlyAccidentDataJson", jsonData); // 변환된 JSON 데이터
//
//		return "accidents/list";
//	}
//
//	/**
//	 * 1 연도별 사고 건수 데이터를 JSON으로 반환하는 API
//	 */
//	@GetMapping("/list/json")
//	public ResponseEntity<String> getYearlyAccidentData() throws JsonProcessingException {
//		List<Object[]> yearlyAccidentData = localAccidentService.getYearlyAccidentCount();
//		log.info("yearlyAccidentData: {}", Arrays.deepToString(yearlyAccidentData.toArray()));
//		String jsonData = new ObjectMapper().writeValueAsString(yearlyAccidentData);
//		return ResponseEntity.ok(jsonData);
//	}
//
//	/**
//	 * 1 선택한 주요 지역(시/도)에 따른 시/군/구 목록 반환
//	 */
//	@GetMapping("/minorRegions")
//	public ResponseEntity<List<String>> getMinorRegions(
//			@RequestParam(value = "majorRegion", required = false) String majorRegion) {
//		log.info(">>> Fetching minor regions for majorRegion: {}", majorRegion);
//		List<String> minorRegions = localAccidentService.getMinorRegionsByMajorRegion(majorRegion);
//		log.info(">>> Found {} minor regions", minorRegions.size());
//		return ResponseEntity.ok(minorRegions);
//	}
//
//	/**
//	 * 1 필터링된 사고 데이터를 JSON으로 반환하는 API
//	 */
//	@GetMapping("/list1/json")
//	public ResponseEntity<Page<LocalAccident>> getAccidentData(
//			@RequestParam(value = "year", required = false) Integer year,
//			@RequestParam(value = "majorRegion", required = false) String majorRegion,
//			@RequestParam(value = "minorRegion", required = false) String minorRegion,
//			@RequestParam(value = "page", defaultValue = "0") int page,
//			@RequestParam(value = "size", defaultValue = "10") int size) {
//
//		log.info(">>> Fetching paged accidents JSON - Year: {}, MajorRegion: {}, MinorRegion: {}, Page: {}, Size: {}",
//				year, majorRegion, minorRegion, page, size);
//
//		Pageable pageable = PageRequest.of(page, size);
//		Page<LocalAccident> accidents = localAccidentService.getAccidentsFilteredPaged(year, majorRegion, minorRegion,
//				pageable);
//
//		log.info(">>> Returning {} accident records as JSON", accidents.getTotalElements());
//		return ResponseEntity.ok(accidents);
//	}
//}