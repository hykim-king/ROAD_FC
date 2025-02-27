package com.pcwk.ehr.DataRepository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pcwk.ehr.data.TrafficComparison;

@Repository
public interface TrafficComparisonRepository extends JpaRepository<TrafficComparison, Long>, JpaSpecificationExecutor<TrafficComparison> {
	
	/**
	 * 연도 목록 조회(중복 제거 후 내림차순 정렬) 
	 */
	@Query("SELECT DISTINCT c.tcStdYear FROM TrafficComparison c ORDER BY c.tcStdYear DESC")
	List<Integer> findDistinctYears();
	
	/**
	 * 설날,추석 각각 데이터 조회
	 */
	@Query("SELECT DISTINCT c.tcSphldfttNm FROM TrafficComparison c ORDER BY c.tcSphldfttNm ASC")
	List<String> findDistinctSphldfttNm();
	
	/**
	 * 각 명절 별 세부 날짜 조회(D-3,D-2,D-1,D,D+1,D+2,D+3)
	 */
	@Query("SELECT DISTINCT c.tcSphldfttScopTypeNm FROM TrafficComparison c ORDER BY c.tcSphldfttScopTypeNm ASC")
	List<String> findDistinctSphldfttScopTypeNm();
		
	/**
	 * 사고 데이터 필터링 조회(연도,명절구분,세부날짜,시간대 필터 적용)
	 */
	@Query("SELECT t FROM TrafficComparison t " +
		       "WHERE (:year IS NULL OR t.tcStdYear = :year) AND " +
		       "(:specialday IS NULL OR t.tcSphldfttNm = :specialday) AND " +
		       "(:specialdaytype IS NULL OR t.tcSphldfttScopTypeNm = :specialdaytype) AND " +
		       "(:hour IS NULL OR t.tcSydHour = :hour) AND " +
		       "(:trfl IS NULL OR t.tcTrfl = :trfl) AND " +
		       "(:prevtrfl IS NULL OR t.tcPrevTrfl = :prevtrfl) AND " +
		       "(:changetrfl IS NULL OR t.tcChangeTrfl = :changetrfl) AND " +
		       "(:ratetrfl IS NULL OR t.tcRateTrfl = :ratetrfl)")
	List<TrafficComparison> findByFilters(@Param("year")Integer year,
										  @Param("specialday")String specialday,
										  @Param("specialdaytype")String specialdaytype,
										  @Param("hour")Integer hour,
										  @Param("trfl")Integer trfl,
										  @Param("prevtrfl")Integer prevtrfl,
										  @Param("changetrfl")Integer changetrfl,
										  @Param("ratetrfl")Integer ratetrfl);
		
	/**
	 * 명절날 구분과 시간대별 사고 데이터 조회
	 */
	List<TrafficComparison> findByTcSphldfttNmAndTcSydHour(String specialday, Integer hour);
}
