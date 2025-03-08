package com.pcwk.ehr.DataRepository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.pcwk.ehr.data.YearAccidentRate;

public interface YearAccidentRateRepository extends JpaRepository<YearAccidentRate, Long>, JpaSpecificationExecutor<YearAccidentRate> {

	/**
	 * 연도별 조회
	 */
	List<YearAccidentRate> findByTaYear(Integer taYear);
	/**
	 * 증가율 범위 검색
	 * @param minRate
	 * @param maxRate
	 * @return
	 */
	List<YearAccidentRate> findByTaAccidentRateBetween(Double minRate, Double maxRate);
	
	List<YearAccidentRate> findByTaDeathRateBetween(Double minRate, Double maxRate);
	
	List<YearAccidentRate> findByTaInjuryRateBetween(Double minRate, Double maxRate);
	
	@Query("SELECT DISTINCT r.taYear FROM YearAccidentRate r ORDER BY r.taYear DESC")
	List<Integer> findDistinctYears();
}