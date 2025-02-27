package com.pcwk.ehr.DataService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	public List<Integer> getAllYears(){
		return yearAccidentRepository.findDistinctYears();
		
	}
	
	/**
	 * 연도별 모든 사고건수
	 * @return
	 */
	public List<Integer> getAllAccidents(){
		return yearAccidentRepository.findAllAccidents();
	}
	
	/**
	 * 연도별 모든 사망건수
	 * @return
	 */
	public List<Integer> getAllDeaths(){
		return yearAccidentRepository.findAllDeaths();
	}
	
	/**
	 * 연도별 모든 부상건수
	 * @return
	 */
	public List<Integer> getAllInjurys(){
		return yearAccidentRepository.findAllInjurys();
	}
}