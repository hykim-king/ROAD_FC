package com.pcwk.ehr.DataService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pcwk.ehr.DataRepository.YearAccidentRepository;
import com.pcwk.ehr.data.YearAccident;
import com.pcwk.ehr.data.YearAccidentRateDTO;

@Service
public class YearAccidentService {

	final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	YearAccidentRepository yearAccidentRepository;

	public YearAccidentService() {
		log.info("┌──────────────────────────┐");
		log.info("│ YearAccidentService()    │");
		log.info("└──────────────────────────┘");
	}

	public List<YearAccident> getAllYearAccidents() {
		return yearAccidentRepository.findAll();
	}

	public List<YearAccidentRateDTO> getAllAccidentsWithRates() {
		return yearAccidentRepository.findAllAccidentsWithRates();
	}

	/**
	 * 연도 목록 조회
	 */
	public List<Integer> getAllYears() {
		return yearAccidentRepository.findDistinctYears();

	}

	/**
	 * 연도별 모든 사망건수
	 * 
	 * @return
	 */
	public List<Integer> getAllDeaths() {
		return yearAccidentRepository.findAllDeaths();
	}

	/**
	 * 연도별 모든 부상건수
	 * 
	 * @return
	 */
	public List<Integer> getAllInjurys() {
		return yearAccidentRepository.findAllInjurys();
	}

	public List<Object[]> getTrafficData() {
		List<Object[]> result = yearAccidentRepository.sumTrafficData();
		return result;
	}

    public Page<YearAccident> getAccidents(Integer tdYear, Integer tdAccident, Integer tdDeathCnt, Integer tdInjuryCnt, Integer tdAccidentRate, Integer taFatalRate, String keyword, Pageable pageable) {
        return yearAccidentRepository.getAccidents(tdYear, tdAccident, tdDeathCnt, tdInjuryCnt, tdAccidentRate, taFatalRate, keyword, pageable);
    }

    public List<YearAccident> getAllAccidents() {
        // 데이터베이스 쿼리를 통해 YearAccident 객체 리스트를 반환하도록 수정
        return yearAccidentRepository.findAll();
    }


}