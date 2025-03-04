package com.pcwk.ehr.DataController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcwk.ehr.DataService.LocalAccidentService;
import com.pcwk.ehr.DataService.TrafficAccidentService;
import com.pcwk.ehr.DataService.TrafficComparisonService;
import com.pcwk.ehr.DataService.WeatherAccidentService;
import com.pcwk.ehr.DataService.YearAccidentRateService;
import com.pcwk.ehr.DataService.YearAccidentService;
import com.pcwk.ehr.data.LocalAccident;
import com.pcwk.ehr.data.TrafficComparison;
import com.pcwk.ehr.data.WeatherAccident;
import com.pcwk.ehr.data.YearAccidentRateDTO;

@Controller
@RequestMapping("/DataChart")
public class DataController {
	final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	LocalAccidentService localAccidentService;

	@Autowired
	TrafficAccidentService trafficAccidentService;

	@Autowired
	TrafficComparisonService trafficComparisonService;

	@Autowired
	WeatherAccidentService weatherAccidentService;

	@Autowired
	YearAccidentRateService yearAccidentRateService;

	@Autowired
	YearAccidentService yearAccidentService;

	/**
	 * 1 연도별 사건사고 데이터
	 */
	@GetMapping("/list")
	public String listAllAccidents(@RequestParam(value = "year", required = false) Integer year,
	        @RequestParam(value = "majorRegion", required = false) String majorRegion,
	        @RequestParam(value = "minorRegion", required = false) String minorRegion,
	        @RequestParam(value = "keyword", required = false) String keyword,
	        @RequestParam(value = "page", defaultValue = "0") int page,
	        @RequestParam(value = "size", defaultValue = "10") int size, Model model) throws JsonProcessingException {

	    Pageable pageable = PageRequest.of(page, size);
	    Page<LocalAccident> pagedAccidents;

	    if (keyword != null && !keyword.isEmpty()) {
	        pagedAccidents = localAccidentService.getPagedLocalAccidents(keyword, page, size);
	    } else if (year != null || (majorRegion != null && !majorRegion.isEmpty())
	            || (minorRegion != null && !minorRegion.isEmpty())) {
	        pagedAccidents = localAccidentService.getAccidentsFilteredPaged(year, majorRegion, minorRegion, pageable);
	    } else {
	        pagedAccidents = localAccidentService.getAllLocalAccidentsPaged(pageable);
	    }

	    List<Integer> yearsList = localAccidentService.getAllYears();
	    List<String> majorRegions = localAccidentService.getAllMajorRegions();
	    List<String> minorRegions = localAccidentService.getAllMinorRegions();
	    List<Object[]> yearlyAccidentData = localAccidentService.getYearlyAccidentCount();

	    // 변환 로직 추가
	    List<Map<String, Object>> transformedData = new ArrayList<>();
	    for (Object[] data : yearlyAccidentData) {
	        Map<String, Object> dataMap = new HashMap<>();
	        dataMap.put("year", data[0]);
	        dataMap.put("accidentCount", data[1]);
	        transformedData.add(dataMap);
	    }

	    // 변환된 데이터를 JSON으로 직렬화하여 반환
	    String jsonData = new ObjectMapper().writeValueAsString(transformedData);

	    model.addAttribute("accidents", pagedAccidents.getContent());
	    model.addAttribute("paging", pagedAccidents);
	    model.addAttribute("years", yearsList);
	    model.addAttribute("majorRegions", majorRegions);
	    model.addAttribute("minorRegions", minorRegions);
	    model.addAttribute("selectedYear", year);
	    model.addAttribute("selectedMajorRegion", majorRegion);
	    model.addAttribute("selectedMinorRegion", minorRegion);
	    model.addAttribute("keyword", keyword);
	    model.addAttribute("yearlyAccidentDataJson", jsonData); // 변환된 JSON 데이터

	    return "accidents/list";
	}


	/**
	 * 1 연도별 사고 건수 데이터를 JSON으로 반환하는 API
	 */
	@GetMapping("/list/json")
	public ResponseEntity<String> getYearlyAccidentData() throws JsonProcessingException {
		List<Object[]> yearlyAccidentData = localAccidentService.getYearlyAccidentCount();
		log.info("yearlyAccidentData: {}", Arrays.deepToString(yearlyAccidentData.toArray()));
		String jsonData = new ObjectMapper().writeValueAsString(yearlyAccidentData);
		return ResponseEntity.ok(jsonData);
	}

	/**
	 * 1 선택한 주요 지역(시/도)에 따른 시/군/구 목록 반환
	 */
	@GetMapping("/minorRegions")
	public ResponseEntity<List<String>> getMinorRegions(
			@RequestParam(value = "majorRegion", required = false) String majorRegion) {
		log.info(">>> Fetching minor regions for majorRegion: {}", majorRegion);
		List<String> minorRegions = localAccidentService.getMinorRegionsByMajorRegion(majorRegion);
		log.info(">>> Found {} minor regions", minorRegions.size());
		return ResponseEntity.ok(minorRegions);
	}

	/**
	 * 1 필터링된 사고 데이터를 JSON으로 반환하는 API
	 */
	@GetMapping("/list1/json")
	public ResponseEntity<Page<LocalAccident>> getAccidentData(
			@RequestParam(value = "year", required = false) Integer year,
			@RequestParam(value = "majorRegion", required = false) String majorRegion,
			@RequestParam(value = "minorRegion", required = false) String minorRegion,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size) {

		log.info(">>> Fetching paged accidents JSON - Year: {}, MajorRegion: {}, MinorRegion: {}, Page: {}, Size: {}",
				year, majorRegion, minorRegion, page, size);

		Pageable pageable = PageRequest.of(page, size);
		Page<LocalAccident> accidents = localAccidentService.getAccidentsFilteredPaged(year, majorRegion, minorRegion,
				pageable);

		log.info(">>> Returning {} accident records as JSON", accidents.getTotalElements());
		return ResponseEntity.ok(accidents);
	}

	/**
	 * 2 명절교통량비교테이블
	 */
	@GetMapping("/clist")
	public String listAllComparison(
	        @RequestParam(value = "year", required = false) Integer year,
	        @RequestParam(value = "specialday", required = false) String specialday,
	        @RequestParam(value = "specialdaytype", required = false) String specialdaytype,
	        @RequestParam(value = "hour", required = false) Integer hour,
	        @RequestParam(value = "trfl", required = false) Integer trfl,
	        @RequestParam(value = "prevtrfl", required = false) Integer prevtrfl,
	        @RequestParam(value = "changetrfl", required = false) Integer changetrfl,
	        @RequestParam(value = "ratetrfl", required = false) Integer ratetrfl,
	        @RequestParam(value = "keyword", required = false) String keyword,
	        @RequestParam(value = "page", defaultValue = "0") int page,
	        @RequestParam(value = "size", defaultValue = "10") int size,
	        Model model) throws JsonProcessingException {

	    Pageable pageable = PageRequest.of(page, size);
	    Page<TrafficComparison> pagedComparison;

	    // 필터링된 데이터를 페이징 처리하여 가져오기
	    pagedComparison = trafficComparisonService.getComparisonFilteredPaged(
	            year, specialday, specialdaytype, hour, trfl, prevtrfl, changetrfl, ratetrfl, keyword, pageable);

	    // 드롭다운 필터 옵션 데이터
	    List<Integer> yearList = trafficComparisonService.getAllYears();
	    List<String> specialdayList = trafficComparisonService.getAllSphldfttNm();
	    List<String> specialdaytypeList = trafficComparisonService.getAllSphldfttScopTypeNm();
	    List<Integer> hourList = IntStream.rangeClosed(0, 23).boxed().collect(Collectors.toList());
	    List<Object[]> comparisonData = trafficComparisonService.getComparisonCount();

	    // 변환 로직 추가
	    List<Map<String, Object>> transData = new ArrayList<>();
	    for (Object[] data : comparisonData) {
	        Map<String, Object> dataMap = new HashMap<>();
	        dataMap.put("year", data[0]);
	        dataMap.put("trfl", data[1]);
	        transData.add(dataMap);
	    }

	    // 변환된 데이터 JSON으로 변환
	    String jsonData = new ObjectMapper().writeValueAsString(transData);

	    // JSON 데이터
	    model.addAttribute("Datajson", jsonData);

	    // 페이징 데이터
	    model.addAttribute("comparisons", pagedComparison.getContent()); // 페이징된 데이터 전달
	    model.addAttribute("paging", pagedComparison); // 페이징 정보 전달

	    // 필터 옵션 데이터
	    model.addAttribute("years", yearList);
	    model.addAttribute("specialday", specialdayList);
	    model.addAttribute("specialdaytype", specialdaytypeList);
	    model.addAttribute("hour", hourList);

	    // 선택한 필터 값 유지
	    model.addAttribute("selectedYear", year);
	    model.addAttribute("selectedDay", specialday);
	    model.addAttribute("selectedType", specialdaytype);
	    model.addAttribute("selectedHour", hour);
	    model.addAttribute("selectedKeyword", keyword);

	    return "accidents/clist";
	}


	@GetMapping("/clist1/json")  
	@ResponseBody 
	public List<Map<String, Object>> getAvgTrafficByFilters(
	        @RequestParam(value = "year", required = false) Integer year,
	        @RequestParam(value = "specialday", required = false) String specialday,
	        @RequestParam(value = "hour", required = false) Integer hour) {
	    List<Map<String, Object>> result = trafficComparisonService.getAvgTrafficByFilters(year, specialday, hour);
	    System.out.println("json 응답 데이터: " + result);
	    return result;
	}

	
	/**
	 * 3 도로날씨별 사고 데이터
	 */
	@GetMapping("/wlist")
	public String listAllWeather(
		    @RequestParam(value = "year", required = false) Integer year,
		    @RequestParam(value = "roadType", required = false) String roadType,
		    @RequestParam(value = "accidentType", required = false) String accidentType,
		    @RequestParam(value = "keyword", required = false) String keyword,
		    @RequestParam(value = "page", defaultValue = "0") int page,
		    @RequestParam(value = "size", defaultValue = "10") int size,
		    Model model) {
	    Page<WeatherAccident> pagedAccidents;
	    Pageable pageable = PageRequest.of(page, size);

	    if (keyword != null && !keyword.isEmpty()) {
	        pagedAccidents = weatherAccidentService.getPagedWeatherAccidents(keyword, page, size);
	    } else if (year != null || (roadType != null && !roadType.isEmpty()) || (accidentType != null && !accidentType.isEmpty())) {
	        pagedAccidents = weatherAccidentService.getAccidentsFilteredPaged(year, roadType, accidentType, pageable);
	    } else {
	        pagedAccidents = weatherAccidentService.getAllWeatherAccidentsPaged(pageable);
	    }

	    List<Integer> yearsList = weatherAccidentService.getAllYears();
	    List<String> roadTypes = weatherAccidentService.getAllRoadType();
	    List<String> accidentTypes = weatherAccidentService.getAllAccidentType();

	    model.addAttribute("accidents", pagedAccidents.getContent());
	    model.addAttribute("paging", pagedAccidents);
	    model.addAttribute("years", yearsList);
	    model.addAttribute("roadTypes", roadTypes);
	    model.addAttribute("accidentTypes", accidentTypes);
	    model.addAttribute("selectedYear", year);
	    model.addAttribute("selectedRoadType", roadType);
	    model.addAttribute("selectedAccidentType", accidentType);
	    model.addAttribute("keyword", keyword);

	    return "accidents/wlist";
	}

	
	
	
	
	
	
	/**
	 * 4. 교통사고데이터
	 */
	@GetMapping("/dlist")
	public String getAllData(@RequestParam(value = "year", required = false) Integer year,
			@RequestParam(value = "accident", required = false) Integer accident,
			@RequestParam(value = "death", required = false) Integer death,
			@RequestParam(value = "injury", required = false) Integer injury,
			@RequestParam(value = "changerate", required = false) Integer changerate,
			@RequestParam(value = "fatalrate", required = false) Integer fatalrate, Model model) {

		List<Integer> allYears = yearAccidentService.getAllYears();
		List<YearAccidentRateDTO> allData = yearAccidentService.getAllAccidentsWithRates();

		model.addAttribute("allYears", allYears);
		model.addAttribute("allData", allData);

		return "accidents/dlist";
	}
}