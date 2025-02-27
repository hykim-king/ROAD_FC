package com.pcwk.ehr.DataRepository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pcwk.ehr.data.WeatherAccident;

@Repository
public interface WeatherAccidentRepository extends JpaRepository<WeatherAccident, Long>, JpaSpecificationExecutor<WeatherAccident> {

	/**
	 * 도로 종류 별 사고 데이터 조회
	 */
	List<WeatherAccident> findByWaRoadType(String waRoadType);

	/**
	 * 사고 건 수가 많은 순으로 정렬하여 전체 사고 데이터 조회
	 */
	List<WeatherAccident> findAllByOrderByWaTotalCntDesc();

	/**
	 * 존재하는 연도 목록 조회
	 */
	@Query("SELECT DISTINCT w.waYear FROM WeatherAccident w ORDER BY w.waYear DESC")
	List<Integer> findDistinctYears();

	/**
	 * 존재하는 도로 종류 조회
	 */
	@Query("SELECT DISTINCT w.waRoadType FROM WeatherAccident w")
	List<String> findDistinctRoadType();

	/**
	 * 사고 종류 구분 조회
	 */
	@Query("SELECT DISTINCT w.waAccidentType FROM WeatherAccident w")
	List<String> findDistinctAccidentType();

	/**
	 * 사고 데이터 필터링 조회(연도,도로종류,부상구분 필터 적용)
	 */
	@Query("SELECT w FROM WeatherAccident w WHERE " + "(:waYear IS NULL OR w.waYear = :waYear) AND "
			+ "(:waRoadType IS NULL OR w.waRoadType = :waRoadType) AND "
			+ "(:waAccidentType IS NULL OR w.waAccidentType = :waAccidentType)")
	List<WeatherAccident> findByFilters(@Param("waYear") Integer waYear, @Param("waRoadType") String waRoadType,
			@Param("waAccidentType") String waAccidentType);
}