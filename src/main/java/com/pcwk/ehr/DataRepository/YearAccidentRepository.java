package com.pcwk.ehr.DataRepository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	@Query("SELECT tdYear, SUM(tdAccident) FROM YearAccident GROUP  BY tdYear ORDER BY tdYear DESC")
	List<Object[]> sumTrafficData();

    @Query("SELECT ya FROM YearAccident ya LEFT JOIN ya.yearAccidentRate yar WHERE " +
            "(:tdYear IS NULL OR ya.tdYear = :tdYear) AND " +
            "(:tdAccident IS NULL OR ya.tdAccident = :tdAccident) AND " +
            "(:tdDeathCnt IS NULL OR ya.tdDeathCnt = :tdDeathCnt) AND " +
            "(:tdInjuryCnt IS NULL OR ya.tdInjuryCnt = :tdInjuryCnt) AND " +
            "(:fatalrate IS NULL OR yar.taFatalRate = :fatalrate) AND " +
            "(:keyword IS NULL OR STR(yar.taAccidentRate) LIKE %:keyword%)") // STR() 함수를 사용하여 정수를 문자열로 변환
     Page<YearAccident> findAccidents(@Param("tdYear") Integer tdYear,
                                      @Param("tdAccident") Integer tdAccident,
                                      @Param("tdDeathCnt") Integer tdDeathCnt,
                                      @Param("tdInjuryCnt") Integer tdInjuryCnt,
                                      @Param("fatalrate") Integer fatalrate,
                                      @Param("keyword") String keyword,
                                      Pageable pageable);
     
    @Query("SELECT ya FROM YearAccident ya LEFT JOIN ya.yearAccidentRate yar WHERE " +
            "(:tdYear IS NULL OR ya.tdYear = :tdYear) AND " +
            "(:tdAccident IS NULL OR ya.tdAccident = :tdAccident) AND " +
            "(:tdDeathCnt IS NULL OR ya.tdDeathCnt = :tdDeathCnt) AND " +
            "(:tdInjuryCnt IS NULL OR ya.tdInjuryCnt = :tdInjuryCnt) AND " +
            "(:taAccidentRate IS NULL OR yar.taAccidentRate = :taAccidentRate) AND " +
            "(:taFatalRate IS NULL OR yar.taFatalRate = :taFatalRate) AND " +
            "(:keyword IS NULL OR STR(yar.taAccidentRate) LIKE %:keyword%)")
     Page<YearAccident> getAccidents(@Param("tdYear") Integer tdYear,
                                     @Param("tdAccident") Integer tdAccident,
                                     @Param("tdDeathCnt") Integer tdDeathCnt,
                                     @Param("tdInjuryCnt") Integer tdInjuryCnt,
                                     @Param("taAccidentRate") Integer taAccidentRate,
                                     @Param("taFatalRate") Integer taFatalRate,
                                     @Param("keyword") String keyword,
                                     Pageable pageable);

     @Query("SELECT ya.tdYear, ya.tdAccident, ya.tdDeathCnt, ya.tdInjuryCnt, yar.taAccidentRate, yar.taDeathRate, yar.taInjuryRate, yar.taFatalRate " +
            "FROM YearAccident ya " +
            "LEFT JOIN ya.yearAccidentRate yar")
     List<Object[]> getAllAccidents();
	
 }