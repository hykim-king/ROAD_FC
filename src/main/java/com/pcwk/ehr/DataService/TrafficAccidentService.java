package com.pcwk.ehr.DataService;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.pcwk.ehr.DataRepository.TrafficAccidentRepository;
import com.pcwk.ehr.data.TrafficAccident;

import jakarta.persistence.criteria.Predicate;

@Service
public class TrafficAccidentService {

	final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	TrafficAccidentRepository trafficAccidentRepository;

	public TrafficAccidentService() {
        log.info("┌─────────────────────────────┐");
        log.info("│ TrafficAccidentService()    │");
        log.info("└─────────────────────────────┘");
	}
	
	/**
	 * 모든 데이터 출력
	 */
	public List<TrafficAccident> getAllTrafficAccidents(){
		return trafficAccidentRepository.findAll();
	}
	
	/**
	 * 연도별 데이터 조회
	 */
	public List<Integer> getAllYears(){
		return trafficAccidentRepository.findDistinctYears();
	}
	
	/**
	 * 요일별 데이터 조회			
	 */
	public List<String> getAllDays(){
		return trafficAccidentRepository.findDistinctDays();
	}
	
	/**
	 * 시간대별 데이터 조회
	 */
	public List<String> getAllTimeZone(){
		return trafficAccidentRepository.findDistinctTimeZone();
	}
	
	/**
	 * 세부사항 필터링 후 데이터 조회
	 */
	public List<TrafficAccident> getAccidentsFilter(Integer year,
													String date,
													String timezone){
		return trafficAccidentRepository.findByFilter(year,date,timezone); 
	}
	
	/**
	 * 페이징
	 */
    public Specification<TrafficAccident> search(String keyword) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.or(
                criteriaBuilder.like(root.get("taAccidentYear").as(String.class), "%" + keyword + "%"),
                criteriaBuilder.like(root.get("taAccidentDate"), "%" + keyword + "%"),
                criteriaBuilder.like(root.get("taTimeZone"), "%" + keyword + "%")
            );
    }
    
    public Page<TrafficAccident> getPagedTrafficAccident(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return trafficAccidentRepository.findAll(search(keyword), pageable);
    }
    
    public Page<TrafficAccident> getAccidentsFilteredPaged(Integer year, String date, String timezone, Pageable pageable) {
        Specification<TrafficAccident> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (year != null) {
                predicates.add(criteriaBuilder.equal(root.get("taAccidentYear"), year));
            }
            if (date != null && !date.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("taAccidentDate"), date));
            }
            if (timezone != null && !timezone.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("taTimeZone"), timezone));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        return trafficAccidentRepository.findAll(spec, pageable);
    }

    public Page<TrafficAccident> getAllTrafficAccidentPaged(Pageable pageable) {
        return trafficAccidentRepository.findAll(pageable);
    }
    
    public List<Object[]> getYearlyAccidentData(Integer year) {
        return trafficAccidentRepository.findYearlyAccidentData(year);
    }
}