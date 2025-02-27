package com.pcwk.ehr.DataRepository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pcwk.ehr.data.LocalAccident;

@Repository
public interface LocalAccidentRepository extends JpaRepository<LocalAccident, Long>, JpaSpecificationExecutor<LocalAccident>  {

	/**
	 * 특정 주요 지역(시/도)과 상세 지역(시/군/구)의 사고 데이터 조회
	 */
	List<LocalAccident> findByLaMajorRegionAndLaMinorRegion(String majorRegion, String minorRegion);

	/**
	 * 사고 건수가 많은 순으로 정렬하여 전체 사고 데이터 조회
	 */
	List<LocalAccident> findAllByOrderByLaLocalCntDesc();

	/**
	 * 존재하는 연도 목록 조회 (중복 제거 후 내림차순 정렬)
	 */
	@Query("SELECT DISTINCT l.laYear FROM LocalAccident l ORDER BY l.laYear DESC")
	List<Integer> findDistinctYears();

	/**
	 * 존재하는 주요 지역(시/도) 목록 조회
	 */
	@Query("SELECT DISTINCT l.laMajorRegion FROM LocalAccident l ORDER BY l.laMajorRegion ASC")
	List<String> findDistinctMajorRegions();

	/**
	 * 존재하는 상세 지역(시/군/구) 목록 조회
	 */
	@Query("SELECT DISTINCT l.laMinorRegion FROM LocalAccident l ORDER BY l.laMinorRegion ASC")
	List<String> findDistinctMinorRegions();

	/**
	 * 선택한 주요 지역(시/도)에 해당하는 상세 지역(시/군/구) 목록 조회
	 */
	@Query("SELECT DISTINCT l.laMinorRegion FROM LocalAccident l WHERE l.laMajorRegion = :majorRegion ORDER BY l.laMinorRegion ASC")
	List<String> findMinorRegionsByMajorRegion(@Param("majorRegion") String majorRegion);

	/**
	 * 모든 시/군/구 목록 조회 (주요 지역 관계없이)
	 */
	@Query("SELECT DISTINCT l.laMinorRegion FROM LocalAccident l ORDER BY l.laMinorRegion ASC")
	List<String> findAllMinorRegions();

	/**
	 * 사고 데이터 필터링 조회 (연도, 주요 지역, 상세 지역 필터 적용)
	 */
	@Query("SELECT l "
		   + "FROM LocalAccident l "
		   + "WHERE " + "(:year IS NULL OR l.laYear = :year) AND "
		   + "(:majorRegion IS NULL OR l.laMajorRegion = :majorRegion) AND "
		   + "(:minorRegion IS NULL OR l.laMinorRegion = :minorRegion)")
	List<LocalAccident> findByFilters(@Param("year") Integer year, 
									  @Param("majorRegion") String majorRegion,
									  @Param("minorRegion") String minorRegion);
	
    // ✨ 연도별 사고 건수 집계 쿼리
    @Query("SELECT la.laYear, COUNT(la) FROM LocalAccident la GROUP BY la.laYear ORDER BY la.laYear")
    List<Object[]> getYearlyAccidentCount();
    
    @Query("SELECT la.laYear, COUNT(la) FROM LocalAccident la GROUP BY la.laYear ORDER BY la.laYear DESC")
    List<Object[]> findYearlyAccidentCount();
  
}