/*
 * package com.pcwk.ehr.DataController;
 * 
 * import java.util.List; import java.util.Map; import
 * java.util.stream.Collectors; import java.util.stream.IntStream;
 * 
 * import org.slf4j.Logger; import org.slf4j.LoggerFactory; import
 * org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.data.domain.Page; import
 * org.springframework.data.domain.PageRequest; import
 * org.springframework.data.domain.Pageable; import
 * org.springframework.stereotype.Controller; import
 * org.springframework.ui.Model; import
 * org.springframework.web.bind.annotation.GetMapping; import
 * org.springframework.web.bind.annotation.RequestMapping; import
 * org.springframework.web.bind.annotation.RequestParam; import
 * org.springframework.web.bind.annotation.ResponseBody;
 * 
 * import com.pcwk.ehr.DataRepository.TrafficComparisonRepository; import
 * com.pcwk.ehr.DataService.TrafficComparisonService; import
 * com.pcwk.ehr.data.TrafficComparison;
 * 
 * @Controller
 * 
 * @RequestMapping("/TrafficComparison") public class
 * TrafficComparisonController {
 * 
 * final Logger log = LoggerFactory.getLogger(getClass());
 * 
 * @Autowired TrafficComparisonService trafficComparisonService;
 * 
 * @Autowired TrafficComparisonRepository trafficComparisonRepository;
 * 
 *//**
	 * 교통 데이터를 리스트화해서 보여줌
	 *//*
		 * @GetMapping("/clist") public String listAllComparison(
		 * 
		 * @RequestParam(value = "year", required = false) Integer year,
		 * 
		 * @RequestParam(value = "specialday", required = false) String specialday,
		 * 
		 * @RequestParam(value = "specialdaytype", required = false) String
		 * specialdaytype,
		 * 
		 * @RequestParam(value = "hour", required = false) Integer hour,
		 * 
		 * @RequestParam(value = "trfl", required = false) Integer trfl,
		 * 
		 * @RequestParam(value = "prevtrfl", required = false) Integer prevtrfl,
		 * 
		 * @RequestParam(value = "changetrfl", required = false) Integer changetrfl,
		 * 
		 * @RequestParam(value = "ratetrfl", required = false) Integer ratetrfl,
		 * 
		 * @RequestParam(value = "page", defaultValue = "0") int page,
		 * 
		 * @RequestParam(value = "size", defaultValue = "10") int size, Model model) {
		 * 
		 * Pageable pageable = PageRequest.of(page, size);
		 * 
		 * // 필터링된 데이터를 페이징 처리하여 가져오기 Page<TrafficComparison> pagedComparison =
		 * trafficComparisonService.getComparisonFilteredPaged(year, specialday,
		 * specialdaytype, hour, trfl, prevtrfl, changetrfl, ratetrfl, pageable);
		 * 
		 * // 드롭다운 필터 옵션 데이터 List<Integer> yearList =
		 * trafficComparisonService.getAllYears(); List<String> specialdayList =
		 * trafficComparisonService.getAllSphldfttNm(); List<String> specialdaytypeList
		 * = trafficComparisonService.getAllSphldfttScopTypeNm(); List<Integer> hourList
		 * = IntStream.rangeClosed(0, 23).boxed().collect(Collectors.toList());
		 * 
		 * // 차트 데이터 준비 List<Integer> years = pagedComparison.getContent().stream()
		 * .map(TrafficComparison::getTcStdYear) .distinct() .sorted()
		 * .collect(Collectors.toList());
		 * 
		 * List<Integer> trafficValues = years.stream() .map(y ->
		 * pagedComparison.getContent().stream() .filter(d ->
		 * d.getTcStdYear().equals(y)) .mapToInt(TrafficComparison::getTcTrfl) .sum())
		 * .collect(Collectors.toList());
		 * 
		 * model.addAttribute("comparisons", pagedComparison.getContent()); // 페이징된 데이터
		 * 전달 model.addAttribute("paging", pagedComparison); // 페이징 정보 전달
		 * 
		 * // 필터 옵션 데이터 model.addAttribute("years", yearList);
		 * model.addAttribute("specialday", specialdayList);
		 * model.addAttribute("specialdaytype", specialdaytypeList);
		 * model.addAttribute("hour", hourList);
		 * 
		 * // 선택한 필터 값 유지 model.addAttribute("selectedYear", year);
		 * model.addAttribute("selectedDay", specialday);
		 * model.addAttribute("selectedType", specialdaytype);
		 * model.addAttribute("selectedHour", hour);
		 * 
		 * // 차트 데이터 전달 model.addAttribute("chartYears", years);
		 * model.addAttribute("chartTrafficValues", trafficValues);
		 * 
		 * return "accidents/clist"; }
		 * 
		 * @GetMapping("/api/data")
		 * 
		 * @ResponseBody public List<TrafficComparison> getComparisonData(
		 * 
		 * @RequestParam(value = "year", required = false) Integer year,
		 * 
		 * @RequestParam(value = "specialday", required = false) String specialday,
		 * 
		 * @RequestParam(value = "specialdaytype", required = false) String
		 * specialdaytype,
		 * 
		 * @RequestParam(value = "hour", required = false) Integer hour) {
		 * 
		 * return trafficComparisonService.getComparisonFiltered(year, specialday,
		 * specialdaytype, hour); }
		 * 
		 * @GetMapping("/api/avgTraffic")
		 * 
		 * @ResponseBody public List<Map<String, Object>> getAvgTrafficByFilters(
		 * 
		 * @RequestParam(value = "year", required = false) Integer year,
		 * 
		 * @RequestParam(value = "specialday", required = false) String specialday,
		 * 
		 * @RequestParam(value = "hour", required = false) Integer hour) {
		 * 
		 * return trafficComparisonService.getAvgTrafficByFilters(year, specialday,
		 * hour); }
		 * 
		 * }
		 */