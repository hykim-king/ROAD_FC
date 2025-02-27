package com.pcwk.ehr.DataService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pcwk.ehr.DataRepository.YearAccidentRateRepository;
import com.pcwk.ehr.data.YearAccidentRate;

@Service
public class YearAccidentRateService {
    
    final Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    YearAccidentRateRepository trafficAccidentRateRepository;

    public List<YearAccidentRate> getAccidentRatesByYear(Integer year) {
        return trafficAccidentRateRepository.findByTaYear(year);
    }

    public List<Integer> getAllYears() {
        return trafficAccidentRateRepository.findDistinctYears();
    }

    public List<YearAccidentRate> getAccidentRatesBetween(Double minRate, Double maxRate) {
        return trafficAccidentRateRepository.findByTaAccidentRateBetween(minRate, maxRate);
    }

    public List<YearAccidentRate> getDeathRatesBetween(Double minRate, Double maxRate) {
        return trafficAccidentRateRepository.findByTaDeathRateBetween(minRate, maxRate);
    }

    public List<YearAccidentRate> getInjuryRatesBetween(Double minRate, Double maxRate) {
        return trafficAccidentRateRepository.findByTaInjuryRateBetween(minRate, maxRate);
    }
}