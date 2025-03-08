package com.pcwk.ehr.map.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pcwk.ehr.SafetyIndexDTO;
import com.pcwk.ehr.freezing.Freezing;
import com.pcwk.ehr.map.entity.Cctv;
import com.pcwk.ehr.map.entity.Road;
import com.pcwk.ehr.map.entity.RoadWeatherInterface;
import com.pcwk.ehr.map.service.MapService;
import com.pcwk.ehr.map.service.SafetyIndexService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/map")
public class MapController {
	@Autowired
	MapService mapService;
	
	@Autowired
	SafetyIndexService safetyIndexService;
	
	@Autowired
	SafetyIndexService testWeatherService;
	
	@GetMapping("/map")
	public String cctvMap(Model model, HttpServletRequest request) {
		log.info("┌──────────────────┐");
		log.info("│ map()            │");
		log.info("└──────────────────┘");
		
		List<Road> accidentList = mapService.findByAccident();
		List<Road> constructionList = mapService.findByConstruction();
		List<RoadWeatherInterface> findNowWeather = mapService.findNowWeather();
		List<Cctv> cctvInfo = mapService.cctvList();
		List<Freezing> freezingInfo = mapService.freezingList();
		List<Cctv> tunnelInfo = mapService.tunnelList();
		List<SafetyIndexDTO> avgSafetyIndex = safetyIndexService.getAvgSafetyIndex();
		List<SafetyIndexDTO> allSafetyIndex = safetyIndexService.getAllSafetyIndex();
		
		log.info("사고 데이터: {}", accidentList);
		log.info("공사 데이터: {}", constructionList);
		log.info("날씨 데이터: {}", findNowWeather);
		log.info("CCTV 데이터: {}", cctvInfo);
		log.info("결빙 데이터: {}", freezingInfo);
		log.info("터널 데이터: {}", tunnelInfo);
		log.info("평균 안전 지수: {}", avgSafetyIndex);
		log.info("전체 안전 지수: {}", allSafetyIndex);
		
		model.addAttribute("accidentList", accidentList);
	    model.addAttribute("constructionList", constructionList);
	    model.addAttribute("findNowWeather", findNowWeather);
	    model.addAttribute("cctvInfo", cctvInfo);
	    model.addAttribute("freezingInfo", freezingInfo);
	    model.addAttribute("tunnelInfo", tunnelInfo);
	    model.addAttribute("avgSafetyIndex", avgSafetyIndex);
	    model.addAttribute("allSafetyIndex", allSafetyIndex);
	    
	    model.addAttribute("currentUrl", request.getRequestURI());
	    
		return "map/map";
	}
}
