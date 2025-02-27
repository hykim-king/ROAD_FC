package com.pcwk.ehr.map.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pcwk.ehr.map.entity.Freezing;
import com.pcwk.ehr.map.service.FreezingService;

@RequestMapping("/map")
@Controller
public class MapController {
	
	@Autowired
	FreezingService freezingService;

	@GetMapping("mainMap")
	public String mainMap(Model model) {
		
		List<Freezing> freezingInfo = freezingService.list();
		 
		model.addAttribute("freezingInfo", freezingInfo);
		
		return "map/mainMap";
	}
	
	@GetMapping("polygonMap")
	public String polygonMap() {
		
		return "map/polygonMap";
	}

}
