package com.pcwk.ehr.news;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NewsService {

	@Autowired
	NewsRepository newsRepository;
	
	public Specification<News> search(String keyword){
		return new Specification<>() {

			@Override
			public Predicate toPredicate(Root<News> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				// 검색 조건 정의
				return criteriaBuilder.or(criteriaBuilder.like(root.get("title"), "%" + keyword + "%"),
										  criteriaBuilder.like(root.get("newsroom"), "%" + keyword + "%")
				);								  
			}			
		};
	}
	
	public Page<News> getList(int page, String keyword){
		log.info("┌──────────────────────┐");
		log.info("│ getList()    		 │");
		log.info("└──────────────────────┘");
		
		log.info("page: {}", page);
		log.info("keyword: {}", keyword);
		
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.asc("id"));
		
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		
		Specification<News> specification = search(keyword);
			
		return newsRepository.findAll(specification, pageable);  
	}
	
	public List<News> getList(){
		log.info("┌──────────────────────┐");
		log.info("│ getList()   no param │");
		log.info("└──────────────────────┘");  
		
		return newsRepository.findAll();
	}
	
}
