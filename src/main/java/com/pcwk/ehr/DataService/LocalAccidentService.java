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

import com.pcwk.ehr.DataRepository.LocalAccidentRepository;
import com.pcwk.ehr.data.LocalAccident;

import jakarta.persistence.criteria.Predicate;

@Service
public class LocalAccidentService {

	final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	LocalAccidentRepository localAccidentRepository;

	public LocalAccidentService() {
		log.info("┌───────────────────────────┐");
		log.info("│ LocalAccidentService()    │");
		log.info("└───────────────────────────┘");
	}

	/**
	 * 모든 사고 데이터 조회
	 */
	public List<LocalAccident> getAllLocalAccidents() {
		return localAccidentRepository.findAll();
	}

	/**
	 * 특정 사고 ID로 데이터 조회
	 */
	public Optional<LocalAccident> getLocalAccidentById(Long id) {
		return localAccidentRepository.findById(id);
	}

	/**
	 * 특정 사고 데이터 삭제
	 */
	public void deleteLocalAccident(Long id) {
		localAccidentRepository.deleteById(id);
	}

	/**
	 * 존재하는 연도 목록 조회
	 */
	public List<Integer> getAllYears() {
		return localAccidentRepository.findDistinctYears();
	}

	/**
	 * 존재하는 주요 지역(시/도) 목록 조회
	 */
	public List<String> getAllMajorRegions() {
		return localAccidentRepository.findDistinctMajorRegions();
	}

	/**
	 * 존재하는 상세 지역(시/군/구) 목록 조회
	 */
	public List<String> getAllMinorRegions() {
		return localAccidentRepository.findDistinctMinorRegions();
	}

	public List<Object[]> getYearlyAccidentCount() {
		List<Object[]> result = localAccidentRepository.sumAccidentsByYear(); // ✅ SUM 사용으로 변경
		log.info("LocalAccidentService - getYearlyAccidentCount: {}", Arrays.deepToString(result.toArray()));
		return result;
	}

	/**
	 * 필터 조건을 적용한 사고 데이터 조회
	 */
	public List<LocalAccident> getAccidentsFiltered(Integer year, String majorRegion, String minorRegion) {
	    List<LocalAccident> result = localAccidentRepository.findByFilters(year, majorRegion, minorRegion);
	    log.info(">>> getAccidentsFiltered - Year: {}, MajorRegion: {}, MinorRegion: {}, Result Size: {}", 
	             year, majorRegion, minorRegion, result.size());
	    return result;
	}


	/**
	 * 선택한 주요 지역(시/도)에 해당하는 상세 지역(시/군/구) 목록 조회
	 */
	public List<String> getMinorRegionsByMajorRegion(String majorRegion) {
		// "특별시", "광역시", "특별자치시" 등을 자동 변환하여 검색 가능하도록 처리
		majorRegion = majorRegion.replaceAll("(특별시|광역시|특별자치시)$", "");

		log.info("Filtered majorRegion: {}", majorRegion);

		return localAccidentRepository.findMinorRegionsByMajorRegion(majorRegion);
	}

	public Specification<LocalAccident> search(String keyword) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.or(
				criteriaBuilder.like(root.get("laYear").as(String.class), "%" + keyword + "%"),
				criteriaBuilder.like(root.get("laMajorRegion"), "%" + keyword + "%"),
				criteriaBuilder.like(root.get("laMinorRegion"), "%" + keyword + "%"),
				criteriaBuilder.like(root.get("laLocalCnt").as(String.class), "%" + keyword + "%"));
	}

	public Page<LocalAccident> getPagedLocalAccidents(String keyword, int page, int size) {
	    Pageable pageable = PageRequest.of(page, size);
	    return localAccidentRepository.findAll(search(keyword), pageable);
	}

	public Page<LocalAccident> getAccidentsFilteredPaged(Integer year, String majorRegion, String minorRegion, Pageable pageable) {
	    Specification<LocalAccident> spec = (root, query, criteriaBuilder) -> {
	        List<Predicate> predicates = new ArrayList<>();

	        if (year != null) {
	            predicates.add(criteriaBuilder.equal(root.get("laYear"), year));
	        }
	        if (majorRegion != null && !majorRegion.isEmpty()) {
	            predicates.add(criteriaBuilder.equal(root.get("laMajorRegion"), majorRegion));
	        }
	        if (minorRegion != null && !minorRegion.isEmpty()) {
	            predicates.add(criteriaBuilder.equal(root.get("laMinorRegion"), minorRegion));
	        }

	        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	    };

	    return localAccidentRepository.findAll(spec, pageable);
	}

	public Page<LocalAccident> getAllLocalAccidentsPaged(Pageable pageable) {
	    return localAccidentRepository.findAll(pageable);
	}
}