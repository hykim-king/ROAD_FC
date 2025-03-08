package com.pcwk.ehr.DataService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;
import com.pcwk.ehr.DataRepository.TrafficComparisonRepository;
import com.pcwk.ehr.data.TrafficComparison;
import com.pcwk.ehr.data.WeatherAccident;

@Service
public class TrafficComparisonService {
	final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	TrafficComparisonRepository trafficComparisonRepository;

	public TrafficComparisonService(TrafficComparisonRepository trafficComparisonRepository) {
		this.trafficComparisonRepository = trafficComparisonRepository;
		log.info("┌───────────────────────────────┐");
		log.info("│ TrafficComparisonService()    │");
		log.info("└───────────────────────────────┘");
	}

	public List<Object[]> getComparisonCount() {
		return trafficComparisonRepository.sumComparisonByYear();
	}

	public List<Integer> getAllYears() {
		return trafficComparisonRepository.findDistinctYears();
	}

	public List<String> getAllSphldfttNm() {
		return trafficComparisonRepository.findDistinctSphldfttNm();
	}

	public List<String> getAllSphldfttScopTypeNm() {
		return trafficComparisonRepository.findDistinctSphldfttScopTypeNm();
	}

	public List<TrafficComparison> getAllTrafficComparisons() {
		return trafficComparisonRepository.findAll();
	}

	public Optional<TrafficComparison> getTrafficComparisonById(Long id) {
		return trafficComparisonRepository.findById(id);
	}

	public void deleteTrafficComparison(Long id) {
		trafficComparisonRepository.deleteById(id);
	}
	
    public Page<TrafficComparison> getComparisonPaged(
            Integer year, 
            String specialday, 
            String specialdaytype, 
            Integer hour, 
            Integer trfl, 
            Integer prevtrfl, 
            Integer changetrfl, 
            Integer ratetrfl, 
            String keyword,
            Pageable pageable) {
        
        Specification<TrafficComparison> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 조건 추가: year 필터
            if (year != null) {
                predicates.add(criteriaBuilder.equal(root.get("tcStdYear"), year));
            }

            // 조건 추가: specialday 필터
            if (specialday != null && !specialday.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("tcSphldfttNm"), specialday));
            }

            // 조건 추가: specialdaytype 필터
            if (specialdaytype != null && !specialdaytype.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("tcSphldfttScopTypeNm"), specialdaytype));
            }

            // 조건 추가: hour 필터
            if (hour != null) {
                predicates.add(criteriaBuilder.equal(root.get("tcSydHour"), hour));
            }

            // 조건 추가: trfl 필터
            if (trfl != null) {
                predicates.add(criteriaBuilder.equal(root.get("tcTrfl"), trfl));
            }

            // 조건 추가: prevtrfl 필터
            if (prevtrfl != null) {
                predicates.add(criteriaBuilder.equal(root.get("tcPrevTrfl"), prevtrfl));
            }

            // 조건 추가: changetrfl 필터
            if (changetrfl != null) {
                predicates.add(criteriaBuilder.equal(root.get("tcChangeTrfl"), changetrfl));
            }

            // 조건 추가: ratetrfl 필터
            if (ratetrfl != null) {
                predicates.add(criteriaBuilder.equal(root.get("tcRateTrfl"), ratetrfl));
            }

            // 조건 추가: keyword 필터 (LIKE 검색)
            if (keyword != null && !keyword.isEmpty()) {
                Predicate keywordPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(root.get("tcStdYear"), "%" + keyword + "%"),
                        criteriaBuilder.like(root.get("tcSphldfttNm"), "%" + keyword + "%"),
                        criteriaBuilder.like(root.get("tcSphldfttScopTypeNm"), "%" + keyword + "%"),
                        criteriaBuilder.like(root.get("tcTrfl"), "%" + keyword + "%")
                );
                predicates.add(keywordPredicate);
            }

            // 모든 조건을 AND로 묶기
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // 필터링된 데이터를 Pageable에 맞게 반환
        return trafficComparisonRepository.findAll(spec, pageable);
    }
	

	public Page<TrafficComparison> getComparisonFilteredPaged(Integer year, String specialday, String specialdaytype,
			Integer hour, Pageable pageable) {

		Specification<TrafficComparison> spec = (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (year != null) {
				predicates.add(criteriaBuilder.equal(root.get("tcStdYear"), year));
			}

			if (specialday != null && !specialday.isEmpty()) {
				predicates.add(criteriaBuilder.equal(root.get("tcSphldfttNm"), specialday));
			}

			if (specialdaytype != null && !specialdaytype.isEmpty()) {
				predicates.add(criteriaBuilder.equal(root.get("tcSphldfttScopTypeNm"), specialdaytype));
			}

			if (hour != null) {
				predicates.add(criteriaBuilder.equal(root.get("tcSydHour"), hour));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

		return trafficComparisonRepository.findAll(spec, pageable);
	}

	public Page<TrafficComparison> getAllTrafficComparisonsPaged(Pageable pageable) {
		return trafficComparisonRepository.findAll(pageable);
	}

	public List<Map<String, Object>> getAvgTrafficByFilters(Integer year, String specialday, String specialdayType,
			Integer hour, Integer prevTrFl, Integer changeTrFl, Integer rateTrFl) {
		return trafficComparisonRepository.findAvgTraffic(year, specialday, specialdayType, hour, prevTrFl, changeTrFl,
				rateTrFl);
	}

}