package com.pcwk.ehr.DataController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pcwk.ehr.DataService.YearAccidentRateService;
import com.pcwk.ehr.DataService.YearAccidentService;
import com.pcwk.ehr.data.YearAccidentRateDTO;

@Controller
@RequestMapping("/AccidentData")
public class YearAccidentDataController {

	@Autowired
	YearAccidentService yearAccidentService;

	@Autowired
	YearAccidentRateService yearAccidentRateService;

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
