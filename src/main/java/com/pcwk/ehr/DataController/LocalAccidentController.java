/*
 * package com.pcwk.ehr.DataController;
 * 
 * import java.util.Arrays; import java.util.List;
 * 
 * import org.slf4j.Logger; import org.slf4j.LoggerFactory; import
 * org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.data.domain.Page; import
 * org.springframework.data.domain.PageRequest; import
 * org.springframework.data.domain.Pageable; import
 * org.springframework.http.ResponseEntity; import
 * org.springframework.stereotype.Controller; import
 * org.springframework.ui.Model; import
 * org.springframework.web.bind.annotation.GetMapping; import
 * org.springframework.web.bind.annotation.RequestMapping; import
 * org.springframework.web.bind.annotation.RequestParam;
 * 
 * import com.fasterxml.jackson.core.JsonProcessingException; import
 * com.fasterxml.jackson.databind.ObjectMapper; import
 * com.pcwk.ehr.DataRepository.LocalAccidentRepository; import
 * com.pcwk.ehr.DataService.LocalAccidentService; import
 * com.pcwk.ehr.data.LocalAccident;
 * 
 * @Controller
 * 
 * @RequestMapping("/LocalAccident") public class LocalAccidentController {
 * 
 * final Logger log = LoggerFactory.getLogger(getClass());
 * 
 * @Autowired LocalAccidentService localAccidentService;
 * 
 * @Autowired LocalAccidentRepository localAccidentRepository;
 * 
 *//**
	 * 모든 사고 데이터를 리스트화하여 View로 전달
	 */
/*
 * 
 * 
 * @GetMapping("/list") public String listAllAccidents(
 * 
 * @RequestParam(value = "year", required = false) Integer year,
 * 
 * @RequestParam(value = "majorRegion", required = false) String majorRegion,
 * 
 * @RequestParam(value = "minorRegion", required = false) String minorRegion,
 * 
 * @RequestParam(value = "keyword", required = false) String keyword,
 * 
 * @RequestParam(value = "page", defaultValue = "0") int page,
 * 
 * @RequestParam(value = "size", defaultValue = "10") int size, Model model)
 * throws JsonProcessingException {
 * 
 * Pageable pageable = PageRequest.of(page, size); Page<LocalAccident>
 * pagedAccidents;
 * 
 * if (keyword != null && !keyword.isEmpty()) { pagedAccidents =
 * localAccidentService.getPagedLocalAccidents(keyword, page, size); } else if
 * (year != null || (majorRegion != null && !majorRegion.isEmpty()) ||
 * (minorRegion != null && !minorRegion.isEmpty())) { pagedAccidents =
 * localAccidentService.getAccidentsFilteredPaged(year, majorRegion,
 * minorRegion, pageable); } else { pagedAccidents =
 * localAccidentService.getAllLocalAccidentsPaged(pageable); }
 * 
 * List<Integer> yearsList = localAccidentService.getAllYears(); List<String>
 * majorRegions = localAccidentService.getAllMajorRegions(); List<String>
 * minorRegions = localAccidentService.getAllMinorRegions(); List<Object[]>
 * yearlyAccidentData = localAccidentService.getYearlyAccidentCount();
 * 
 * log.info("yearlyAccidentData: {}",
 * Arrays.deepToString(yearlyAccidentData.toArray()));
 * 
 * model.addAttribute("accidents", pagedAccidents.getContent());
 * model.addAttribute("paging", pagedAccidents); model.addAttribute("years",
 * yearsList); model.addAttribute("majorRegions", majorRegions);
 * model.addAttribute("minorRegions", minorRegions);
 * model.addAttribute("selectedYear", year);
 * model.addAttribute("selectedMajorRegion", majorRegion);
 * model.addAttribute("selectedMinorRegion", minorRegion);
 * model.addAttribute("keyword", keyword);
 * model.addAttribute("yearlyAccidentDataJson", new
 * ObjectMapper().writeValueAsString(yearlyAccidentData));
 * 
 * return "accidents/list"; }
 * 
 *//**
	 * 연도별 사고 건수 데이터를 JSON으로 반환하는 API
	 */
/*
 * 
 * 
 * @GetMapping("/list/json") public ResponseEntity<String>
 * getYearlyAccidentData() throws JsonProcessingException { List<Object[]>
 * yearlyAccidentData = localAccidentService.getYearlyAccidentCount();
 * log.info("yearlyAccidentData: {}",
 * Arrays.deepToString(yearlyAccidentData.toArray())); String jsonData = new
 * ObjectMapper().writeValueAsString(yearlyAccidentData); return
 * ResponseEntity.ok(jsonData); }
 * 
 *//**
	 * 선택한 주요 지역(시/도)에 따른 시/군/구 목록 반환
	 */
/*
 * 
 * 
 * @GetMapping("/minorRegions") public ResponseEntity<List<String>>
 * getMinorRegions(@RequestParam(value = "majorRegion", required = false) String
 * majorRegion) { log.info(">>> Fetching minor regions for majorRegion: {}",
 * majorRegion); List<String> minorRegions =
 * localAccidentService.getMinorRegionsByMajorRegion(majorRegion);
 * log.info(">>> Found {} minor regions", minorRegions.size()); return
 * ResponseEntity.ok(minorRegions); }
 * 
 *//**
	 * 필터링된 사고 데이터를 JSON으로 반환하는 API
	 *//*
		 * @GetMapping("/list1/json") public ResponseEntity<Page<LocalAccident>>
		 * getAccidentData(
		 * 
		 * @RequestParam(value = "year", required = false) Integer year,
		 * 
		 * @RequestParam(value = "majorRegion", required = false) String majorRegion,
		 * 
		 * @RequestParam(value = "minorRegion", required = false) String minorRegion,
		 * 
		 * @RequestParam(value = "page", defaultValue = "0") int page,
		 * 
		 * @RequestParam(value = "size", defaultValue = "10") int size) {
		 * 
		 * log.
		 * info(">>> Fetching paged accidents JSON - Year: {}, MajorRegion: {}, MinorRegion: {}, Page: {}, Size: {}"
		 * , year, majorRegion, minorRegion, page, size);
		 * 
		 * Pageable pageable = PageRequest.of(page, size); Page<LocalAccident> accidents
		 * = localAccidentService.getAccidentsFilteredPaged(year, majorRegion,
		 * minorRegion, pageable);
		 * 
		 * log.info(">>> Returning {} accident records as JSON",
		 * accidents.getTotalElements()); return ResponseEntity.ok(accidents); }
		 * 
		 * }
		 */