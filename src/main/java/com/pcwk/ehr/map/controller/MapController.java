package com.pcwk.ehr.map.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/map")
@Controller
public class MapController {

	@GetMapping("mainMap")
	public String mainMap() {
		
		return "map/mainMap";
	}
	
	@GetMapping("polygonMap")
	public String polygonMap() {
		
		return "map/polygonMap";
	}
}
