package com.pcwk.ehr.DataRepository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pcwk.ehr.data.TrafficComparison;

@Repository
public interface TrafficComparisonRepository extends JpaRepository<TrafficComparison, Long>, JpaSpecificationExecutor<TrafficComparison> {
	
	@Query("SELECT new map(c.tcStdYear as year, c.tcSphldfttNm as specialday, c.tcSydHour as hour, " +
		       "AVG(c.tcTrfl) as avgTraffic, AVG(c.tcPrevTrfl) as prevTraffic, AVG(c.tcChangeTrfl) as changeTraffic, AVG(c.tcRateTrfl) as rateTraffic) " +
		       "FROM TrafficComparison c " +
		       "WHERE (:year IS NULL OR c.tcStdYear = :year) " +
		       "AND (:specialday IS NULL OR c.tcSphldfttNm = :specialday) " +
		       "AND (:hour IS NULL OR c.tcSydHour = :hour) " +
		       "GROUP BY c.tcStdYear, c.tcSphldfttNm, c.tcSydHour")
		List<Map<String, Object>> findAvgTraffic(
		        @Param("year") Integer year,
		        @Param("specialday") String specialday,
		        @Param("hour") Integer hour);


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
	
    @Query("SELECT t FROM TrafficComparison t WHERE " +
            "(:year IS NULL OR t.tcStdYear = :year) AND " +
            "(:specialday IS NULL OR t.tcSphldfttNm = :specialday) AND " +
            "(:specialdaytype IS NULL OR t.tcSphldfttScopTypeNm = :specialdaytype) AND " +
            "(:hour IS NULL OR t.tcSydHour = :hour)")
     List<TrafficComparison> findFilteredTrafficComparisons(
             @Param("year") Integer year,
             @Param("specialday") String specialday,
             @Param("specialdaytype") String specialdaytype,
             @Param("hour") Integer hour);

    @Query("SELECT t FROM TrafficComparison t WHERE " +
            "(:year IS NULL OR t.tcStdYear = :year) AND " +
            "(:specialday IS NULL OR t.tcSphldfttNm = :specialday) AND " +
            "(:specialdaytype IS NULL OR t.tcSphldfttScopTypeNm = :specialdaytype) AND " +
            "(:hour IS NULL OR t.tcSydHour = :hour)")
	Page<TrafficComparison> findFilteredTrafficComparisons(
            @Param("year") Integer year,
            @Param("specialday") String specialday,
            @Param("specialdaytype") String specialdaytype,
            @Param("hour") Integer hour,
            Pageable pageable);
    
    // 명절별 평균 교통량, 증가율 등을 추가
    @Query("SELECT t.tcStdYear, t.tcSphldfttNm, AVG(t.tcTrfl) AS avgTraffic, AVG(t.tcRateTrfl) AS avgRate " +
    	       "FROM TrafficComparison t " +
    	       "WHERE (:year IS NULL OR t.tcStdYear = :year) " +
    	       "AND (:holiday IS NULL OR t.tcSphldfttNm = :holiday) " +
    	       "AND (:hour IS NULL OR t.tcSydHour = :hour) " +
    	       "GROUP BY t.tcStdYear, t.tcSphldfttNm " +
    	       "ORDER BY t.tcStdYear DESC")
    	List<Object[]> findAvgTrafficByFilters(@Param("year") Integer year,
    	                                       @Param("holiday") String holiday,
    	                                       @Param("hour") Integer hour);

    @Query("SELECT t.tcStdYear, SUM(t.tcTrfl) FROM TrafficComparison t GROUP BY t.tcStdYear ORDER BY t.tcStdYear DESC")
    List<Object[]> sumComparisonByYear();
    	
    	
 }