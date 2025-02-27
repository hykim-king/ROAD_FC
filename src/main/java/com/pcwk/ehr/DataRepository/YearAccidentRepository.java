package com.pcwk.ehr.DataRepository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pcwk.ehr.data.YearAccident;
import com.pcwk.ehr.data.YearAccidentRateDTO;

public interface YearAccidentRepository extends JpaRepository<YearAccident, Long>, JpaSpecificationExecutor<YearAccident>{

	/**
	 * 연도 목록 조회
	 */
	@Query("SELECT DISTINCT d.tdYear FROM YearAccident d ORDER BY d.tdYear DESC")
	List<Integer> findDistinctYears();
	
	/**
	 * 전체 데이터 조회
	 */
	List<YearAccident> findAll();
	
	/**
	 * 연도별 사고 데이터 조회
	 */
	@Query("SELECT tdAccident,tdDeathCnt,tdInjuryCnt FROM YearAccident WHERE tdYear = :year")
	List<Object[]> findYearAccidentData(@Param("year")Integer year);
	  
	/**
	 * 부상,사망,사고 건 수 
	 */	
	@Query("SELECT DISTINCT tdAccident FROM YearAccident")
	List<Integer> findAllAccidents();
	@Query("SELECT DISTINCT tdDeathCnt FROM YearAccident")
	List<Integer> findAllDeaths();
	@Query("SELECT DISTINCT tdInjuryCnt FROM YearAccident")
	List<Integer> findAllInjurys();
	
	@Query("SELECT new com.pcwk.ehr.data.YearAccidentRateDTO(y.tdYear, y.tdAccident, y.tdDeathCnt, y.tdInjuryCnt, COALESCE(r.taAccidentRate, 0), COALESCE(r.taDeathRate, 0), COALESCE(r.taFatalRate, 0), COALESCE(r.taInjuryRate, 0)) " +
		       "FROM YearAccident y LEFT JOIN YearAccidentRate r ON y.tdYear = r.taYear")
	List<YearAccidentRateDTO> findAllAccidentsWithRates();



}