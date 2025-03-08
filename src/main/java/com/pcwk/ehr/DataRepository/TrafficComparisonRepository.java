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
public interface TrafficComparisonRepository
		extends JpaRepository<TrafficComparison, Long>, JpaSpecificationExecutor<TrafficComparison> {

	// 평균 교통량과 다른 데이터 필터링 + 페이징
	@Query("SELECT new map(tc.tcStdYear as year, tc.tcSphldfttNm as specialDay, tc.tcSphldfttScopTypeNm as specialDayType, "
			+ "tc.tcSydHour as hour, AVG(tc.tcTrfl) as avgTraffic, AVG(tc.tcPrevTrfl) as prevTraffic, AVG(tc.tcChangeTrfl) as changeTraffic, AVG(tc.tcRateTrfl) as rateTraffic) "
			+ "FROM TrafficComparison tc " + "WHERE (:year IS NULL OR tc.tcStdYear = :year) "
			+ "AND (:specialday IS NULL OR tc.tcSphldfttNm = :specialday) "
			+ "AND (:specialdayType IS NULL OR tc.tcSphldfttScopTypeNm = :specialdayType) "
			+ "AND (:hour IS NULL OR tc.tcSydHour = :hour) " + "AND (:prevTrfl IS NULL OR tc.tcPrevTrfl = :prevTrfl) "
			+ "AND (:changeTrfl IS NULL OR tc.tcChangeTrfl = :changeTrfl) "
			+ "AND (:rateTrfl IS NULL OR tc.tcRateTrfl = :rateTrfl) "
			+ "GROUP BY tc.tcStdYear, tc.tcSphldfttNm, tc.tcSphldfttScopTypeNm, tc.tcSydHour")
	Page<Map<String, Object>> findAvgTrafficWithPagination(@Param("year") Integer year,
			@Param("specialday") String specialday, @Param("specialdayType") String specialdayType,
			@Param("hour") Integer hour, @Param("prevTrfl") Integer prevTrfl, @Param("changeTrfl") Integer changeTrfl,
			@Param("rateTrfl") Double rateTrfl, Pageable pageable);

	// 필터링된 TrafficComparison 데이터 + 페이징
	@Query("SELECT t FROM TrafficComparison t " + "WHERE (:year IS NULL OR t.tcStdYear = :year) "
			+ "AND (:specialday IS NULL OR t.tcSphldfttNm = :specialday) "
			+ "AND (:specialdayType IS NULL OR t.tcSphldfttScopTypeNm = :specialdayType) "
			+ "AND (:hour IS NULL OR t.tcSydHour = :hour) " + "AND (:prevTrfl IS NULL OR t.tcPrevTrfl = :prevTrfl) "
			+ "AND (:changeTrfl IS NULL OR t.tcChangeTrfl = :changeTrfl) "
			+ "AND (:rateTrfl IS NULL OR t.tcRateTrfl = :rateTrfl)")
	Page<TrafficComparison> findFilteredTrafficComparisonsWithPagination(@Param("year") Integer year,
			@Param("specialday") String specialday, @Param("specialdayType") String specialdayType,
			@Param("hour") Integer hour, @Param("prevTrfl") Integer prevTrfl, @Param("changeTrfl") Integer changeTrfl,
			@Param("rateTrfl") Double rateTrfl, Pageable pageable);

	@Query("SELECT t.tcStdYear, SUM(t.tcTrfl) FROM TrafficComparison t GROUP BY t.tcStdYear ORDER BY t.tcStdYear DESC")
	List<Object[]> sumComparisonByYear();

	@Query("SELECT DISTINCT t.tcStdYear FROM TrafficComparison t ORDER BY t.tcStdYear DESC")
	List<Integer> findDistinctYears();

	@Query("SELECT DISTINCT t.tcSphldfttNm FROM TrafficComparison t ORDER BY t.tcSphldfttNm ASC")
	List<String> findDistinctSphldfttNm();

	@Query("SELECT DISTINCT t.tcSphldfttScopTypeNm FROM TrafficComparison t ORDER BY t.tcSphldfttScopTypeNm ASC")
	List<String> findDistinctSphldfttScopTypeNm();

	@Query("SELECT new map(c.tcStdYear as year, c.tcSphldfttNm as specialday, c.tcSydHour as hour, "
			+ "AVG(c.tcTrfl) as avgTraffic, AVG(c.tcPrevTrfl) as prevTraffic, AVG(c.tcChangeTrfl) as changeTraffic, AVG(c.tcRateTrfl) as rateTraffic) "
			+ "FROM TrafficComparison c " + "WHERE (:year IS NULL OR c.tcStdYear = :year) "
			+ "AND (:specialday IS NULL OR c.tcSphldfttNm = :specialday) "
			+ "AND (:specialdayType IS NULL OR c.tcSphldfttScopTypeNm = :specialdayType) "
			+ "AND (:hour IS NULL OR c.tcSydHour = :hour) " + "AND (:prevTrFl IS NULL OR c.tcPrevTrfl = :prevTrFl) " + // 수정:
																														// Optional
																														// 처리
			"AND (:changeTrFl IS NULL OR c.tcChangeTrfl = :changeTrFl) "
			+ "AND (:rateTrFl IS NULL OR c.tcRateTrfl = :rateTrFl) "
			+ "GROUP BY c.tcStdYear, c.tcSphldfttNm, c.tcSydHour")
	List<Map<String, Object>> findAvgTraffic(@Param("year") Integer year, @Param("specialday") String specialday,
			@Param("specialdayType") String specialdayType, @Param("hour") Integer hour,
			@Param("prevTrFl") Integer prevTrFl, // Integer 타입으로 변경
			@Param("changeTrFl") Integer changeTrFl, @Param("rateTrFl") Integer rateTrFl);
	
	
	
    @Query("SELECT t FROM TrafficComparison t " +
            "WHERE (:year IS NULL OR t.tcStdYear = :year) " +
            "AND (:specialday IS NULL OR t.tcSphldfttNm = :specialday) " +
            "AND (:specialdayType IS NULL OR t.tcSphldfttScopTypeNm = :specialdayType) " +
            "AND (:hour IS NULL OR t.tcSydHour = :hour)")
     List<TrafficComparison> findFilteredTrafficData(
             @Param("year") Integer year,
             @Param("specialday") String specialday,
             @Param("specialdayType") String specialdayType,
             @Param("hour") Integer hour,
             Pageable pageable);
 }