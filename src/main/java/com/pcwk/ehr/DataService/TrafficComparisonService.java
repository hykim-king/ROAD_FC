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

    public Page<TrafficComparison> getComparisonFilteredPaged(
            Integer year, String specialday, String specialdaytype, Integer hour,
            Integer trfl, Integer prevtrfl, Integer changetrfl, Integer ratetrfl,
            String keyword, Pageable pageable) {
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
            if (keyword != null && !keyword.isEmpty()) {
                Predicate yearPredicate = criteriaBuilder.like(root.get("tcStdYear"), "%" + keyword + "%");
                Predicate specialdayPredicate = criteriaBuilder.like(root.get("tcSphldfttNm"), "%" + keyword + "%");
                Predicate specialdaytypePredicate = criteriaBuilder.like(root.get("tcSphldfttScopTypeNm"), "%" + keyword + "%");
                Predicate trflPredicate = criteriaBuilder.like(root.get("tcTrfl"), "%" + keyword + "%");
                Predicate combinedPredicate = criteriaBuilder.or(yearPredicate, specialdayPredicate, specialdaytypePredicate, trflPredicate);
                predicates.add(combinedPredicate);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return trafficComparisonRepository.findAll(spec, pageable);
    }

    public List<Map<String, Object>> getAvgTrafficByFilters(Integer year, String specialday, Integer hour) {
        return trafficComparisonRepository.findAvgTraffic(year, specialday, hour);
    }
}