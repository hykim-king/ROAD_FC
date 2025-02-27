package com.pcwk.ehr.DataController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pcwk.ehr.DataRepository.TrafficComparisonRepository;
import com.pcwk.ehr.DataService.TrafficComparisonService;
import com.pcwk.ehr.data.TrafficComparison;

@Controller
@RequestMapping("/TrafficComparison")
public class TrafficComparisonController {

	final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	TrafficComparisonService trafficComparisonService;

	@Autowired
	TrafficComparisonRepository trafficComparisonRepository;

	/**
	 * 교통 데이터를 리스트화해서 보여줌
	 */
	@GetMapping("/clist")
	public String listAllComparison(@RequestParam(value = "year", required = false) Integer year,
			@RequestParam(value = "specialday", required = false) String specialday,
			@RequestParam(value = "specialdaytype", required = false) String specialdaytype,
			@RequestParam(value = "hour", required = false) Integer hour,
			@RequestParam(value = "trfl", required = false) Integer trfl,
			@RequestParam(value = "prevtrfl", required = false) Integer prevtrfl,
			@RequestParam(value = "changetrfl", required = false) Integer changetrfl,
			@RequestParam(value = "ratetrfl", required = false) Integer ratetrfl,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size, Model model) {

		Page<TrafficComparison> pagedComparison;
		Pageable pageable = PageRequest.of(page, size);

	    if (keyword != null && !keyword.isEmpty()) {
	        pagedComparison = trafficComparisonService.getPagedTrafficComparison(keyword, page, size);
	    } else if (year != null || (specialday != null && !specialday.isEmpty())
	            || (specialdaytype != null && !specialdaytype.isEmpty()) || hour != null) {
	        pagedComparison = trafficComparisonService.getTrafficComparisonFilteredPaged(year, specialday,
	                specialdaytype, hour, pageable);
	    } else {
	        pagedComparison = trafficComparisonService.getAllTrafficComparisonPaged(pageable);
	    }

		List<TrafficComparison> filteredData = trafficComparisonService.getComparisonFiltered(year, specialday,
				specialdaytype, hour, trfl, prevtrfl, changetrfl, ratetrfl);

		List<Integer> yearList = trafficComparisonService.getAllYears();
		List<String> specialdayList = trafficComparisonService.getAllSphldfttNm();
		List<String> specialdaytypeList = trafficComparisonService.getAllSphldfttScopTypeNm();
		List<Integer> hourList = IntStream.rangeClosed(0, 23).boxed().collect(Collectors.toList());

		model.addAttribute("comparisons", filteredData);
		model.addAttribute("years", yearList);
		model.addAttribute("specialday", specialdayList);
		model.addAttribute("specialdaytype", specialdaytypeList);
		model.addAttribute("hour", hourList);
		model.addAttribute("keyword", keyword);
		
	    model.addAttribute("comparisons", pagedComparison.getContent());
	    model.addAttribute("paging", pagedComparison);

		// 선택된 값 유지 기능
		model.addAttribute("selectedYear", year);
		model.addAttribute("selectedDay", specialday);
		model.addAttribute("selectedType", specialdaytype);
		model.addAttribute("selectedHour", hour);

		return "accidents/clist";
	}
}