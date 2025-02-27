package com.pcwk.ehr.DataRepository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pcwk.ehr.data.TrafficAccident;

public interface TrafficAccidentRepository extends JpaRepository<TrafficAccident, Long>, JpaSpecificationExecutor<TrafficAccident>{

	/**
	 * 연도 목록 조회
	 */
	@Query("SELECT DISTINCT t.taAccidentYear FROM TrafficAccident t ORDER BY t.taAccidentYear DESC")
	List<Integer> findDistinctYears();
	
	/**
	 * 날짜 목록 조회
	 */
	@Query("SELECT DISTINCT t.taAccidentDate FROM TrafficAccident t ORDER BY t.taAccidentDate DESC")
	List<String> findDistinctDays();
	
	/**
	 * 시간대 조회
	 */
	@Query("SELECT DISTINCT t.taTimeZone FROM TrafficAccident t ORDER BY t.taTimeZone DESC")
	List<String> findDistinctTimeZone();
	
	/**
	 * 전체 사고 데이터 조회
	 */
	List<TrafficAccident> findAllByOrderByTaAccidentCnt();
	
	/**
	 * 필터링 데이터
	 * @param year
	 * @param date
	 * @param timezone
	 * @return
	 */
	@Query("SELECT c FROM TrafficAccident c " +
		       "WHERE (:year IS NULL OR c.taAccidentYear = :year) " +
		       "AND (:date IS NULL OR c.taAccidentDate = :date) " +
		       "AND (:timezone IS NULL OR c.taTimeZone = :timezone)")
	List<TrafficAccident> findByFilter(@Param("year") Integer year,
		                               @Param("date") String date,
		                               @Param("timezone") String timezone);


	/**
     * 연도별 사고 데이터 조회
     * @param year
     * @return
     */
    @Query("SELECT taAccidentCnt, taDeathTotalCnt, taInjuryTotalCnt FROM TrafficAccident WHERE taAccidentYear = :year")
    List<Object[]> findYearlyAccidentData(@Param("year") Integer year);
}
