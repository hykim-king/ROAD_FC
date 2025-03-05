package com.pcwk.ehr.DataService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.pcwk.ehr.DataRepository.WeatherAccidentRepository;
import com.pcwk.ehr.data.WeatherAccident;

import jakarta.persistence.criteria.Predicate;

@Service
public class WeatherAccidentService {

	final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	WeatherAccidentRepository weatherAccidentRepository;

	public WeatherAccidentService() {
        log.info("┌───────────────────────────┐");
        log.info("│ WeatherAccidentService()  │");
        log.info("└───────────────────────────┘");
	}
	
	/**
	 * 모든 사고 데이터 조회
	 */
	public List<WeatherAccident> getAllWeatherAccidents(){
		return weatherAccidentRepository.findAll();
	}
	
	/**
	 * 특정 사고 ID로 데이터 조회
	 */
	public Optional<WeatherAccident> getWeatherAccidentById(Long id){
		return weatherAccidentRepository.findById(id);
	}
	
	/**
	 * 특정 사고 데이터 삭제
	 */
	public void deleteWeatherAccident(Long id) {
		weatherAccidentRepository.deleteById(id);
	}
	
	/**
	 * 존재하는 연도 목록 조회
	 */
	public List<Integer> getAllYears(){
		return weatherAccidentRepository.findDistinctYears();
	}
	
	/**
	 * 존재하는 도로 종류 조회
	 */
	public List<String> getAllRoadType(){
		return weatherAccidentRepository.findDistinctRoadType();
	}
	
	/**
	 * 사고 종류 구분지는 코드 조회
	 */
	public List<String> getAllAccidentType(){
		return weatherAccidentRepository.findDistinctAccidentType();
	}
	
	/**
	 * 필터 조건을 적용한 데이터 조회
	 */
	public List<WeatherAccident> getAccidentsFiltered(Integer year,String waRoadType,String waAccidentType){
		return weatherAccidentRepository.findByFilters(year, waRoadType, waAccidentType);
	}
	

	    public Page<WeatherAccident> getPagedWeatherAccidents(String keyword, int page, int size) {
	        Pageable pageable = PageRequest.of(page, size);
	        return weatherAccidentRepository.findAll(search(keyword), pageable);
	    }

	    public Page<WeatherAccident> getAccidentsFilteredPaged(Integer year, String roadType, String accidentType, Pageable pageable) {
	        Specification<WeatherAccident> spec = (root, query, criteriaBuilder) -> {
	            List<Predicate> predicates = new ArrayList<>();
	            
	            if (year != null) {
	                predicates.add(criteriaBuilder.equal(root.get("waYear"), year));
	            }
	            if (roadType != null && !roadType.isEmpty()) {
	                predicates.add(criteriaBuilder.equal(root.get("waRoadType"), roadType));
	            }
	            if (accidentType != null && !accidentType.isEmpty()) {
	                predicates.add(criteriaBuilder.equal(root.get("waAccidentType"), accidentType));
	            }
	            
	            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	        };
	        
	        return weatherAccidentRepository.findAll(spec, pageable);
	    }

	    public Page<WeatherAccident> getAllWeatherAccidentsPaged(Pageable pageable) {
	        return weatherAccidentRepository.findAll(pageable);
	    }

	    private Specification<WeatherAccident> search(String keyword) {
	        return (root, query, criteriaBuilder) -> 
	            criteriaBuilder.or(
	                criteriaBuilder.like(root.get("waYear").as(String.class), "%" + keyword + "%"),
	                criteriaBuilder.like(root.get("waRoadType"), "%" + keyword + "%"),
	                criteriaBuilder.like(root.get("waAccidentType"), "%" + keyword + "%"),
	                criteriaBuilder.like(root.get("waTotalCnt").as(String.class), "%" + keyword + "%")
	            );
	    }
	    
	    public List<Object[]> getYearlyWeatherCount(){
	    	List<Object[]> result = weatherAccidentRepository.sumWeatherAccidentsByYear();
	    	log.info("sumWeatherAccidentsByYear:{}",Arrays.deepToString(result.toArray()));
	    	return result;
	    }
	    
	    
	    
	}