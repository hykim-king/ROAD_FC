package com.pcwk.ehr.DataController;

import com.pcwk.ehr.DataRepository.WeatherAccidentRepository;
import com.pcwk.ehr.DataService.WeatherAccidentService;
import com.pcwk.ehr.data.WeatherAccident;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/WeatherAccident")
public class WeatherAccidentController {
	
	final Logger log = LoggerFactory.getLogger(getClass());
	  
	@Autowired
	WeatherAccidentService weatherAccidentService;
	
	@Autowired
	WeatherAccidentRepository weatherAccidentRepository;

	public WeatherAccidentController() {
		super();
		log.info("┌───────────────────────────────────────┐");
		log.info("│ WeatherAccidentController()           │");
		log.info("└───────────────────────────────────────┘");
	}

	/**
	 * 사고 데이터를 리스트화해서 출력
	 */
	@GetMapping("/wlist")
	public String listAllAccident(
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


}