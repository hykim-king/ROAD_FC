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
import com.pcwk.ehr.data.YearAccident;
import com.pcwk.ehr.data.YearAccidentRate;
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

	    //필터링된 데이터를 페이징 처리하여 가져오기
	    pagedComparison = trafficComparisonService.getComparisonFilteredPaged(
	            year, specialday, specialdaytype, hour, trfl, prevtrfl, changetrfl, ratetrfl, keyword, pageable);

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
	    
	    model.addAttribute("comparisonData",comparisonData);

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
            @RequestParam(value = "specialdaytype", required = false) String specialdayType,
            @RequestParam(value = "hour", required = false) Integer hour,
            @RequestParam(value = "prevtrfl", required = false) Integer prevTrFl,
            @RequestParam(value = "changetrfl", required = false) Integer changeTrFl,
            @RequestParam(value = "ratetrfl", required = false) Double rateTrFl,
            @RequestParam(value = "page", defaultValue = "0") int page,   // 페이지 번호 (0부터 시작)
            @RequestParam(value = "size", defaultValue = "10") int size) { // 페이지 크기

        log.info("getAvgTrafficByFilters called with year={}, specialday={}, specialdaytype={}, hour={}, prevtrfl={}, changetrfl={}, ratetrfl={}",
                 year, specialday, specialdayType, hour, prevTrFl, changeTrFl, rateTrFl);

        // Pageable 생성
        Pageable pageable = PageRequest.of(page, size);

        // 서비스 호출하여 필터링된 데이터 반환
        List<Map<String, Object>> result = (List<Map<String, Object>>) trafficComparisonService.getAvgTrafficByFilters(
                year, specialday, specialdayType, hour, prevTrFl, changeTrFl, rateTrFl, pageable);

        log.info("Filtered result data: {}", result);
        
        return result;
    }


	
	/**
	 * 3 도로날씨별 사고 데이터
	 * @throws JsonProcessingException 
	 */
	@GetMapping("/wlist")
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
	    model.addAttribute("paging", pagedAccidents);
	    model.addAttribute("years", yearsList);
	    model.addAttribute("roadTypes", roadTypes);
	    model.addAttribute("accidentTypes", accidentTypes);
	    model.addAttribute("selectedYear", year);
	    model.addAttribute("selectedRoadType", roadType);
	    model.addAttribute("selectedAccidentType", accidentType);
	    model.addAttribute("keyword", keyword);
	    model.addAttribute("getYearlyWeatherDataJson", new ObjectMapper().writeValueAsString(transData));


	    return "accidents/wlist";
	}

	@GetMapping("/wlist/json")
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


	@GetMapping("/wlist1/json")
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
    @GetMapping("/dlist")
    public String getYearData(@RequestParam(value = "year", required = false) Integer tdYear,
                              @RequestParam(value = "accident", required = false) Integer tdAccident,
                              @RequestParam(value = "death", required = false) Integer tdDeathCnt,
                              @RequestParam(value = "injury", required = false) Integer tdInjuryCnt,
                              @RequestParam(value = "changerate", required = false) Integer taAccidentRate,
                              @RequestParam(value = "fatalrate", required = false) Integer taFatalRate,
                              @RequestParam(value = "keyword", required = false) String keyword,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size, Model model) throws JsonProcessingException {

        Pageable pageable = PageRequest.of(page, size);

        // 데이터 조회
        Page<YearAccident> pageAccidents = yearAccidentService.getAccidents(tdYear, tdAccident, tdDeathCnt, tdInjuryCnt, taAccidentRate, taFatalRate, keyword, pageable);

        List<Integer> allYears = yearAccidentService.getAllYears();
        List<YearAccidentRateDTO> allData = yearAccidentService.getAllAccidentsWithRates();
        List<Object[]> trafficData = yearAccidentService.getTrafficData();

        List<Map<String, Object>> transData = new ArrayList<>();
        for (Object[] data : trafficData) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("year", data[0]);
            dataMap.put("trafficCount", data[1]);
            transData.add(dataMap);
        }

        String jsonData = new ObjectMapper().writeValueAsString(transData);

        model.addAttribute("accidents", pageAccidents.getContent());
        model.addAttribute("allYears", allYears);
        model.addAttribute("allData", allData);
        model.addAttribute("allDataJson", jsonData); // 변경된 JSON 데이터
        model.addAttribute("trafficData", jsonData);

        return "accidents/dlist";
    }

    @GetMapping("/dlist/json")
    public ResponseEntity<List<Map<String, Object>>> getYearTrafficData() {
        List<Object[]> getYearTrafficData = yearAccidentService.getAllAccidents();
        List<Map<String, Object>> transData = new ArrayList<>();

        for (Object[] data : getYearTrafficData) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("year", data[0]);
            dataMap.put("trafficCount", data[1]);
            transData.add(dataMap);
        }
        return ResponseEntity.ok(transData);
    }

    @GetMapping("/dlist1/json")
    public ResponseEntity<List<Map<String, Object>>> getYearData
    							(@RequestParam(value = "year", required = false) Integer year,
                                 @RequestParam(value = "accident", required = false) Integer accident,
                                 @RequestParam(value = "death", required = false) Integer death,
                                 @RequestParam(value = "injury", required = false) Integer injury,
                                 @RequestParam(value = "fatalrate", required = false) Integer fatalrate,
                                 @RequestParam(value = "keyword", required = false) String keyword,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "10") int size, Model model) throws JsonProcessingException {

        Pageable pageable = PageRequest.of(page, size);
        Page<YearAccident> pageAccidents = yearAccidentService.getAccidents(year, accident, death, injury, null, fatalrate, keyword, pageable);

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
            responseData.put("taDeathRate", rate != null ? rate.getTaDeathRate() : null);
            responseData.put("taInjuryRate", rate != null ? rate.getTaInjuryRate() : null);
            responseData.put("taFatalRate", rate != null ? rate.getTaFatalRate() : null);
            responseData.put("taAccidentRate", rate != null ? rate.getTaAccidentRate() : null);
            return responseData;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

}