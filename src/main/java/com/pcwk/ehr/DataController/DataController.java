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
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcwk.ehr.DataService.LocalAccidentService;
import com.pcwk.ehr.DataService.TrafficComparisonService;
import com.pcwk.ehr.DataService.WeatherAccidentService;
import com.pcwk.ehr.DataService.YearAccidentRateService;
import com.pcwk.ehr.DataService.YearAccidentService;
import com.pcwk.ehr.data.LocalAccident;
import com.pcwk.ehr.data.TrafficComparison;
import com.pcwk.ehr.data.WeatherAccident;
import com.pcwk.ehr.data.YearAccident;
import com.pcwk.ehr.data.YearAccidentRate;
import com.pcwk.ehr.data.DataController.DataUtil;

List<Map<String,Object>> transData = DataUtil.convertToMapList(yearlyAccidentData,"accidentCount");
String jsonData = new ObjectMapper().writeValueAsString(transData);

@Controller
@RequestMapping("/DataChart")
public class DataController {
	final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	LocalAccidentService localAccidentService;

	@Autowired
	TrafficComparisonService trafficComparisonService;

	@Autowired
	WeatherAccidentService weatherAccidentService;

	@Autowired
	YearAccidentRateService yearAccidentRateService;

	@Autowired
	YearAccidentService yearAccidentService;
	
	/**
	 * 공용 페이징 기능
	 */
	
	
	/**
	 * 1 연도별 사건사고 데이터
	 */
	/**
	 * 1 연도별 사건사고 데이터
	 */
	@GetMapping("/localAccidentData")
	public String listAllAccidents(@RequestParam(value = "year", required = false) Integer year,
	        @RequestParam(value = "majorRegion", required = false) String majorRegion,
	        @RequestParam(value = "minorRegion", required = false) String minorRegion,
	        @RequestParam(value = "keyword", required = false) String keyword,
	        @RequestParam(value = "page", defaultValue = "0") int page,
	        @RequestParam(value = "size", defaultValue = "10") int size, 
	        Model model) throws JsonProcessingException {
  
		Page<LocalAccident> pagedAccidents;
	    Pageable pageable = PageRequest.of(page, size);

	    if (keyword != null && !keyword.isEmpty()) {
	        pagedAccidents = localAccidentService.getPagedLocalAccidents(keyword, page, size);
	    } else if (year != null || (majorRegion != null && !majorRegion.isEmpty()) || (minorRegion != null && !minorRegion.isEmpty())) {
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
	    model.addAttribute("DataJson", jsonData); // 변환된 JSON 데이터
	    model.addAttribute("paging", pagedAccidents);
	    model.addAttribute("years", localAccidentService.getAllYears());
	    model.addAttribute("majorRegions", localAccidentService.getAllMajorRegions());
	    model.addAttribute("minorRegions", localAccidentService.getAllMinorRegions());
	    model.addAttribute("selectedYear", year);
	    model.addAttribute("selectedMajorRegion", majorRegion);
	    model.addAttribute("selectedMinorRegion", minorRegion);
	    model.addAttribute("keyword", keyword);
	    model.addAttribute("yearlyAccidentDataJson", new ObjectMapper().writeValueAsString(transformedData));

	    return "accidents/localAccidentData";
	}

	/**
	 * 1 연도별 사고 건수 데이터를 JSON으로 반환하는 API
	 */
	@GetMapping("/localAccidentData/json")
	public ResponseEntity<List<Map<String, Object>>> getYearlyAccidentData() throws JsonProcessingException {
		List<Object[]> yearlyAccidentData = localAccidentService.getYearlyAccidentCount();
		List<Map<String, Object>> transData = new ArrayList<>();
		
		for(Object[] data : yearlyAccidentData) {
			Map<String, Object> dataMap = new HashMap<>();
			dataMap.put("year", data[0]);
			dataMap.put("localCnt", data[1]);
			transData.add(dataMap);
		}
		
		return ResponseEntity.ok(transData);
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
	@GetMapping("/localAccidentData1/json")
	public ResponseEntity<List<LocalAccident>> getAccidentData(
			@RequestParam(value = "year", required = false) Integer year,
			@RequestParam(value = "majorRegion", required = false) String majorRegion,
			@RequestParam(value = "minorRegion", required = false) String minorRegion,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size) {
		
		Pageable pageable = PageRequest.of(page, size);
		Page<LocalAccident> pagedAccidents;
		
		if (year == null && (majorRegion == null || majorRegion.isEmpty()) && (minorRegion == null || minorRegion.isEmpty())) {  
			pagedAccidents = localAccidentService.getAllLocalAccidentsPaged(pageable);
		} else {
			pagedAccidents = localAccidentService.getAccidentsFilteredPaged(year, majorRegion, minorRegion, pageable);
		}
		
  
        log.info("Filtered Data: {}", pagedAccidents.getContent());

		return ResponseEntity.ok(pagedAccidents.getContent());
	}

	/**
	 * 2 명절교통량비교테이블
	 */
	@GetMapping("/comparisonData")
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

		Page<TrafficComparison> pagedComparison;
	    Pageable pageable = PageRequest.of(page, size);
	    
	    if (keyword != null && !keyword.isEmpty()) {
	        pagedComparison = trafficComparisonService.getComparisonPaged(year, specialday, specialdaytype, hour, trfl, prevtrfl, changetrfl, ratetrfl, keyword, pageable);
	    } else if (year != null || (specialday != null && !specialday.isEmpty()) || (specialdaytype != null && !specialdaytype.isEmpty())
	            || (hour != null)) {
	        pagedComparison = trafficComparisonService.getComparisonFilteredPaged(year, specialday, specialdaytype, hour, pageable);
	    } else {
	        pagedComparison = trafficComparisonService.getAllTrafficComparisonsPaged(pageable);
	    }

	    // 드롭다운 필터 옵션 데이터
	    List<Integer> yearList = trafficComparisonService.getAllYears();
	    List<String> specialdayList = trafficComparisonService.getAllSphldfttNm().stream().distinct().collect(Collectors.toList());
	    log.info("Special day list: {}",specialdayList); 
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
	    
	    model.addAttribute("comparisons", pagedComparison.getContent()); // 페이징된 데이터 전달
	    model.addAttribute("Datajson", jsonData);
	    model.addAttribute("paging", pagedComparison); // 페이징 정보 전달
	    model.addAttribute("years", yearList);
	    model.addAttribute("specialday", specialdayList);
	    model.addAttribute("specialdaytype", specialdaytypeList);
	    model.addAttribute("hour", hourList);
	    model.addAttribute("selectedYear", year);
	    model.addAttribute("selectedDay", specialday);
	    model.addAttribute("selectedType", specialdaytype);
	    model.addAttribute("selectedHour", hour);
	    model.addAttribute("selectedKeyword", keyword);
	    model.addAttribute("comparisonDataJson",new ObjectMapper().writeValueAsString(comparisonData));

	    return "accidents/comparisonData";
	}
	
	@GetMapping("/clist/json")
	public ResponseEntity<List<Map<String, Object>>> comparisonData(){
		List<Object[]> comparisonData = trafficComparisonService.getComparisonCount();
		List<Map<String, Object>> transData = new ArrayList<>();
		
		for(Object[] data : comparisonData) {
			Map<String,Object> dataMap = new HashMap<>();
			dataMap.put("year",data[0]);
	        dataMap.put("trfl", data[1]);
	        transData.add(dataMap);
		}
		return ResponseEntity.ok(transData);
	}


	@GetMapping("/comparisonData1/json")
	@ResponseBody
	public ResponseEntity<List<TrafficComparison>> comparisonData(
	    @RequestParam(value = "year", required = false) Integer year,
	    @RequestParam(value = "specialday", required = false) String specialday,
	    @RequestParam(value = "specialdaytype", required = false) String specialdaytype,
	    @RequestParam(value = "hour", required = false) Integer hour,
	    @RequestParam(value = "page", defaultValue = "0") int page,
	    @RequestParam(value = "size", defaultValue = "10") int size) {

	    Pageable pageable = PageRequest.of(page, size);
	    Page<TrafficComparison> pagedAccidents;

	    // 필터 조건 확인 및 서비스 호출
	    if (year == null && (specialday == null || specialday.isEmpty()) 
	        && (specialdaytype == null || specialdaytype.isEmpty()) 
	        && hour == null) {
	        // 모든 필터가 없는 경우
	        pagedAccidents = trafficComparisonService.getAllTrafficComparisonsPaged(pageable);
	    } else {
	        // 필터가 하나라도 있는 경우
	        pagedAccidents = trafficComparisonService.getComparisonFilteredPaged(year, specialday, specialdaytype, hour, pageable);
	    }

	    // 로그 추가
	    log.info("Filtering with year: {}, specialday: {}, specialdaytype: {}, hour: {}", 
	             year != null ? year : "All", 
	             specialday != null ? specialday : "All", 
	             specialdaytype != null ? specialdaytype : "All", 
	             hour != null ? hour : "All");

	    // 필터링된 데이터 로그
	    log.info("Filtered Data: {}", pagedAccidents.getContent());


	    return ResponseEntity.ok(pagedAccidents.getContent()); // List<Map<String, Object>>를 반환
	}




	/**
	 * 3 도로날씨별 사고 데이터
	 * @throws JsonProcessingException 
	 */
	@GetMapping("/weatherAccidentData")
	public String listAllWeather(
		    @RequestParam(value = "year", required = false) Integer year,
		    @RequestParam(value = "roadType", required = false) String roadType,
		    @RequestParam(value = "accidentType", required = false) String accidentType,
		    @RequestParam(value = "keyword", required = false) String keyword,
		    @RequestParam(value = "page", defaultValue = "0") int page,
		    @RequestParam(value = "size", defaultValue = "10") int size,
		    Model model) throws JsonProcessingException {
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
	    List<Object[]> getYearlyWeatherData = weatherAccidentService.getYearlyWeatherCount();
	    
	    List<Map<String, Object>> transData = new ArrayList<>();
	    for (Object[] data: getYearlyWeatherData) {
	    	Map<String, Object> dataMap = new HashMap<>();
	    	dataMap.put("year", data[0]);
	    	dataMap.put("accidentCount", data[1]);
	    	transData.add(dataMap);
	    }

	    String jsonData = new ObjectMapper().writeValueAsString(transData);
	    
	    model.addAttribute("accidents", pagedAccidents.getContent());
	    model.addAttribute("Datajson",jsonData);
	    model.addAttribute("paging", pagedAccidents);
	    model.addAttribute("years", yearsList);
	    model.addAttribute("roadTypes", roadTypes);
	    model.addAttribute("accidentTypes", accidentTypes);
	    model.addAttribute("selectedYear", year);
	    model.addAttribute("selectedRoadType", roadType);
	    model.addAttribute("selectedAccidentType", accidentType);
	    model.addAttribute("keyword", keyword);
	    model.addAttribute("getYearlyWeatherDataJson", new ObjectMapper().writeValueAsString(transData));


	    return "accidents/weatherAccidentData";
	}

	@GetMapping("/weatherAccidentData/json")
	public ResponseEntity<List<Map<String, Object>>> getYearlyWeatherData() {
	    List<Object[]> getYearlyWeatherData = weatherAccidentService.getYearlyWeatherCount();
	    List<Map<String, Object>> transData = new ArrayList<>();
	    
	    for (Object[] data : getYearlyWeatherData) {
	        Map<String, Object> dataMap = new HashMap<>();
	        dataMap.put("year", data[0]);
	        dataMap.put("accidentCount", data[1]);
	        transData.add(dataMap);
	    }

	    return ResponseEntity.ok(transData);
	}


	@GetMapping("/weatherAccidentData1/json")
	public ResponseEntity<List<WeatherAccident>> getWeatherData(
	        @RequestParam(value = "year", required = false) Integer year,
	        @RequestParam(value = "roadType", required = false) String roadType,
	        @RequestParam(value = "accidentType", required = false) String accidentType,
	        @RequestParam(value = "page", defaultValue = "0") int page,
	        @RequestParam(value = "size", defaultValue = "10") int size) {
	    
	    Pageable pageable = PageRequest.of(page, size);
	    Page<WeatherAccident> pagedAccidents;

	    if (year == null && (roadType == null || roadType.isEmpty()) && (accidentType == null || accidentType.isEmpty())) {
	        pagedAccidents = weatherAccidentService.getAllWeatherAccidentsPaged(pageable);
	    } else {
	        pagedAccidents = weatherAccidentService.getAccidentsFilteredPaged(year, roadType, accidentType, pageable);
	    }

        // 로그 추가
        log.info("Filtering with year: {}, roadType: {}, accidentType: {}", year, roadType, accidentType);
        log.info("Filtered Data: {}", pagedAccidents.getContent());
	    
	    
	    return ResponseEntity.ok(pagedAccidents.getContent()); // 🔹 getContent() 사용하여 리스트 반환
	}

	/**
	 * 4. 교통사고데이터
	 */
	@GetMapping("/datalist")
	public String listYearData(@RequestParam(value = "year", required = false) Integer tdYear,
			@RequestParam(value = "accident", required = false) Integer tdAccident,
			@RequestParam(value = "death", required = false) Integer tdDeathCnt,
			@RequestParam(value = "injury", required = false) Integer tdInjuryCnt,
			@RequestParam(value = "changerate", required = false) Integer taAccidentRate,
			@RequestParam(value = "fatalrate", required = false) Integer taFatalRate,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size, Model model) throws JsonProcessingException {
		
		Pageable pageable = PageRequest.of(page, size);
		
		// 페이지별 데이터 조회
		Page<YearAccident> pageAccidents = yearAccidentService.getAccidents(tdYear, tdAccident, tdDeathCnt, tdInjuryCnt, taAccidentRate, taFatalRate, keyword, pageable);
		
		// 전체 데이터 로드
		List<YearAccident> allAccidents = yearAccidentService.getAllAccidents(); // 전체 데이터 조회 메서드
		
		List<Map<String, Object>> transData = new ArrayList<>();
		for (YearAccident data : allAccidents) {
			Map<String, Object> dataMap = new HashMap<>();
			dataMap.put("year", data.getTdYear());
			dataMap.put("trafficCount", data.getTdAccident());
			transData.add(dataMap);
		}
		
		String jsonData = new ObjectMapper().writeValueAsString(transData);
		
		model.addAttribute("accidents", pageAccidents.getContent()); // 페이지별 데이터 추가
		model.addAttribute("allYears", yearAccidentService.getAllYears());
		model.addAttribute("allData", yearAccidentService.getAllAccidentsWithRates());
		model.addAttribute("allDataJson", jsonData); // 전체 데이터 JSON 추가
		model.addAttribute("paging", pageAccidents);  // ✅ 페이징 객체 추가

		return "accidents/datalist";
	}
	
	
	
	@GetMapping("/datalist/json")
	public ResponseEntity<List<Map<String, Object>>> getYearTrafficData() {
		List<YearAccident> getYearTrafficData = yearAccidentService.getAllAccidents();
		List<Map<String, Object>> transData = new ArrayList<>();
		
		for (YearAccident data : getYearTrafficData) {
			Map<String, Object> dataMap = new HashMap<>();
			dataMap.put("year", data.getTdYear());
			dataMap.put("trafficCount", data.getTdAccident());
			transData.add(dataMap);
		}
		return ResponseEntity.ok(transData);
	}
	
	@GetMapping("/datalist1/json")
	public ResponseEntity<List<Map<String, Object>>> getYearData
	(@RequestParam(value = "year", required = false) Integer year,
			@RequestParam(value = "accident", required = false) Integer accident,
			@RequestParam(value = "death", required = false) Integer death,
			@RequestParam(value = "injury", required = false) Integer injury,
			@RequestParam(value = "accidentrate", required = false) Integer accidentrate,
			@RequestParam(value = "fatalrate", required = false) Integer fatalrate,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size, Model model) throws JsonProcessingException {
		
		Pageable pageable = PageRequest.of(page, size);
		Page<YearAccident> pageAccidents = yearAccidentService.getAccidents(year, accident, death, injury, accidentrate, fatalrate, keyword, pageable);
		
		// JSON 응답 구성
		List<Map<String, Object>> result = pageAccidents.stream().map(data -> {
			YearAccidentRate rate = data.getYearAccidentRate();
			Map<String, Object> responseData = new HashMap<>();
			responseData.put("tdId", data.getTdId());
			responseData.put("tdYear", data.getTdYear());
			responseData.put("tdAccident", data.getTdAccident());
			responseData.put("tdDeathCnt", data.getTdDeathCnt());
			responseData.put("tdInjuryCnt", data.getTdInjuryCnt());
			responseData.put("tdRegDt", data.getTdRegDt());
			responseData.put("taAccidentRate", rate != null ? rate.getTaAccidentRate() : null);
			responseData.put("taDeathRate", rate != null ? rate.getTaDeathRate() : null);
			responseData.put("taInjuryRate", rate != null ? rate.getTaInjuryRate() : null);
			responseData.put("taFatalRate", rate != null ? rate.getTaFatalRate() : null);
			return responseData;
		}).collect(Collectors.toList());
		
		return ResponseEntity.ok(result);
	}

}
