package com.pcwk.ehr.DataService;

import java.util.ArrayList;
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

import com.pcwk.ehr.DataRepository.TrafficComparisonRepository;
import com.pcwk.ehr.data.TrafficComparison;

import jakarta.persistence.criteria.Predicate;

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

	/**
	 * 모든 데이터 조회
	 */
	public List<TrafficComparison> getAllTrafficComparisons() {
		return trafficComparisonRepository.findAll();
	}

	/**
	 * 특정 ID로 데이터 조회
	 */
	public Optional<TrafficComparison> getTrafficComparisonById(Long id) {
		return trafficComparisonRepository.findById(id);
	}

	/**
	 * 특정 데이터 삭제
	 */
	public void deleteTrafficComparison(Long id) {
		trafficComparisonRepository.deleteById(id);
	}

	/**
	 * 연도 목록 조회
	 */
	public List<Integer> getAllYears() {
		return trafficComparisonRepository.findDistinctYears();
	}

	/**
	 * 명절 조회
	 */
	public List<String> getAllSphldfttNm() {
		return trafficComparisonRepository.findDistinctSphldfttNm();
	}

	/**
	 * 세부 날짜 조회
	 */
	public List<String> getAllSphldfttScopTypeNm() {
		return trafficComparisonRepository.findDistinctSphldfttScopTypeNm();
	}

	/**
	 * 특정 조건을 적용한 데이터 조회
	 */
	public List<TrafficComparison> getComparisonFiltered(Integer year, String specialday, String specialdaytype,
			Integer hour, Integer trfl, Integer prevtrfl, Integer changetrfl, Integer ratetrfl) {
		return trafficComparisonRepository.findByFilters(year, specialday, specialdaytype, hour, trfl, prevtrfl,
				changetrfl, ratetrfl);
	}

	public Specification<TrafficComparison> search(String keyword) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.or(
				criteriaBuilder.like(root.get("tcStdYear").as(String.class), "%" + keyword + "%"),
				criteriaBuilder.like(root.get("tcSphldfttNm"), "%" + keyword + "%"),
				criteriaBuilder.like(root.get("tcSphldfttScopTypeNm"), "%" + keyword + "%"),
				criteriaBuilder.like(root.get("tcTrfl").as(String.class), "%" + keyword + "%"));
	}

	/**
	 * 키워드 검색과 페이징을 적용한 TrafficComparison 조회
	 */
	public Page<TrafficComparison> getPagedTrafficComparison(String keyword, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return trafficComparisonRepository.findAll(search(keyword), pageable);
	}

	/**
	 * 필터링과 페이징을 적용한 TrafficComparison 조회
	 */
	public Page<TrafficComparison> getTrafficComparisonFilteredPaged(Integer year, String specialday,
			String specialdaytype, Integer hour, Pageable pageable) {
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

	/**
	 * 전체 TrafficComparison 데이터 페이징 조회
	 */
	public Page<TrafficComparison> getAllTrafficComparisonPaged(Pageable pageable) {
		return trafficComparisonRepository.findAll(pageable);
	}
}