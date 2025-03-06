//package com.pcwk.ehr.DataController;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
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
//import com.pcwk.ehr.data.YearAccident;
//import com.pcwk.ehr.data.YearAccidentRate;
// 
//
//
//@Controller
//@RequestMapping("/DataChart")
//public class YearAccidentDataController{
//		@Autowired
//		LocalAccidentService localAccidentService;
//		
//		@Autowired
//		TrafficAccidentService trafficAccidentService;
//		
//		@Autowired
//		TrafficComparisonService trafficComparisonService;
//		
//		@Autowired
//		WeatherAccidentService weatherAccidentService;
//		
//		@Autowired
//		YearAccidentRateService yearAccidentRateService;
//		
//		@Autowired
//		YearAccidentService yearAccidentService;
//		
//		@GetMapping("/dlist")
//		public String listYearData(@RequestParam(value = "year", required = false) Integer tdYear,
//				@RequestParam(value = "accident", required = false) Integer tdAccident,
//				@RequestParam(value = "death", required = false) Integer tdDeathCnt,
//				@RequestParam(value = "injury", required = false) Integer tdInjuryCnt,
//				@RequestParam(value = "changerate", required = false) Integer taAccidentRate,
//				@RequestParam(value = "fatalrate", required = false) Integer taFatalRate,
//				@RequestParam(value = "keyword", required = false) String keyword,
//				@RequestParam(value = "page", defaultValue = "0") int page,
//				@RequestParam(value = "size", defaultValue = "10") int size, Model model) throws JsonProcessingException {
//			
//			Pageable pageable = PageRequest.of(page, size);
//			
//			// 페이지별 데이터 조회
//			Page<YearAccident> pageAccidents = yearAccidentService.getAccidents(tdYear, tdAccident, tdDeathCnt, tdInjuryCnt, taAccidentRate, taFatalRate, keyword, pageable);
//			
//			// 전체 데이터 로드
//			List<YearAccident> allAccidents = yearAccidentService.getAllAccidents(); // 전체 데이터 조회 메서드
//			
//			List<Map<String, Object>> transData = new ArrayList<>();
//			for (YearAccident data : allAccidents) {
//				Map<String, Object> dataMap = new HashMap<>();
//				dataMap.put("year", data.getTdYear());
//				dataMap.put("trafficCount", data.getTdAccident());
//				transData.add(dataMap);
//			}
//			
//			String jsonData = new ObjectMapper().writeValueAsString(transData);
//			
//			model.addAttribute("accidents", pageAccidents.getContent()); // 페이지별 데이터 추가
//			model.addAttribute("allYears", yearAccidentService.getAllYears());
//			model.addAttribute("allData", yearAccidentService.getAllAccidentsWithRates());
//			model.addAttribute("allDataJson", jsonData); // 전체 데이터 JSON 추가
//			return "accidents/dlist";
//		}
//		
//		
//		
//		@GetMapping("/dlist/json")
//		public ResponseEntity<List<Map<String, Object>>> getYearTrafficData() {
//			List<YearAccident> getYearTrafficData = yearAccidentService.getAllAccidents();
//			List<Map<String, Object>> transData = new ArrayList<>();
//			
//			for (YearAccident data : getYearTrafficData) {
//				Map<String, Object> dataMap = new HashMap<>();
//				dataMap.put("year", data.getTdYear());
//				dataMap.put("trafficCount", data.getTdAccident());
//				transData.add(dataMap);
//			}
//			return ResponseEntity.ok(transData);
//		}
//		
//		@GetMapping("/dlist1/json")
//		public ResponseEntity<List<Map<String, Object>>> getYearData
//		(@RequestParam(value = "year", required = false) Integer year,
//				@RequestParam(value = "accident", required = false) Integer accident,
//				@RequestParam(value = "death", required = false) Integer death,
//				@RequestParam(value = "injury", required = false) Integer injury,
//				@RequestParam(value = "accidentrate", required = false) Integer accidentrate,
//				@RequestParam(value = "fatalrate", required = false) Integer fatalrate,
//				@RequestParam(value = "keyword", required = false) String keyword,
//				@RequestParam(value = "page", defaultValue = "0") int page,
//				@RequestParam(value = "size", defaultValue = "10") int size, Model model) throws JsonProcessingException {
//			
//			Pageable pageable = PageRequest.of(page, size);
//			Page<YearAccident> pageAccidents = yearAccidentService.getAccidents(year, accident, death, injury, accidentrate, fatalrate, keyword, pageable);
//			
//			// JSON 응답 구성
//			List<Map<String, Object>> result = pageAccidents.stream().map(data -> {
//				YearAccidentRate rate = data.getYearAccidentRate();
//				Map<String, Object> responseData = new HashMap<>();
//				responseData.put("tdId", data.getTdId());
//				responseData.put("tdYear", data.getTdYear());
//				responseData.put("tdAccident", data.getTdAccident());
//				responseData.put("tdDeathCnt", data.getTdDeathCnt());
//				responseData.put("tdInjuryCnt", data.getTdInjuryCnt());
//				responseData.put("tdRegDt", data.getTdRegDt());
//				responseData.put("taAccidentRate", rate != null ? rate.getTaAccidentRate() : null);
//				responseData.put("taDeathRate", rate != null ? rate.getTaDeathRate() : null);
//				responseData.put("taInjuryRate", rate != null ? rate.getTaInjuryRate() : null);
//				responseData.put("taFatalRate", rate != null ? rate.getTaFatalRate() : null);
//				return responseData;
//			}).collect(Collectors.toList());
//			
//			return ResponseEntity.ok(result);
//	}
// }