package com.pcwk.ehr.news;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/news")
@Controller
@Slf4j
public class NewsController {

	@Autowired
	NewsService newsService;

	public NewsController() {
		log.info("┌──────────────────────┐");
		log.info("│ NewsController()     │");
		log.info("└──────────────────────┘");
	}
	
	@GetMapping(value = "/list")  
	public String list(Model model, @RequestParam(value ="page", defaultValue = "0") int page,
			@RequestParam(value = "keyword", defaultValue = "") String keyword,
			HttpServletRequest request) {
		
		Page<News> paging = newsService.getList(page, keyword);
		
		model.addAttribute("paging", paging);       
		model.addAttribute("keyword", keyword);       
		model.addAttribute("currentUrl", request.getRequestURI());
		log.info("size: " + paging.getSize());
		log.info("데이터 총 갯수:" + paging.getTotalElements());
		
		for(News news: paging.getContent()) {
			System.out.println(news.getId() + ": " + news.getUrl());
		}
			
		
		return "news/news";    
	}
}