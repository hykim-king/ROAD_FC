package com.pcwk.ehr.DataController;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcwk.ehr.DataRepository.LocalAccidentRepository;
import com.pcwk.ehr.DataService.LocalAccidentService;
import com.pcwk.ehr.data.LocalAccident;

@Controller
@RequestMapping("/LocalAccident")
public class LocalAccidentController {

    final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    LocalAccidentService localAccidentService;
    
    @Autowired
    LocalAccidentRepository localAccidentRepository;

    /**
     * 모든 사고 데이터를 리스트화해서 보여줌
     * 
     * @param model
     * @return
     * @throws JsonProcessingException 
     */
    @GetMapping("/list")
    public String listAllAccidents(
        @RequestParam(value = "year", required = false) Integer year,
        @RequestParam(value = "majorRegion", required = false) String majorRegion,
        @RequestParam(value = "minorRegion", required = false) String minorRegion,
        @RequestParam(value = "keyword", required = false) String keyword,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        Model model) throws JsonProcessingException {

        Pageable pageable = PageRequest.of(page, size);
        Page<LocalAccident> pagedAccidents;

        if (keyword != null && !keyword.isEmpty()) {
            pagedAccidents = localAccidentService.getPagedLocalAccidents(keyword, page, size);
        } else if (year != null || (majorRegion != null && !majorRegion.isEmpty()) || (minorRegion != null && !minorRegion.isEmpty())) {
            pagedAccidents = localAccidentService.getAccidentsFilteredPaged(year, majorRegion, minorRegion, pageable);
        } else {
            pagedAccidents = localAccidentService.getAllLocalAccidentsPaged(pageable);
        }

        List<Integer> yearsList = localAccidentService.getAllYears();
        List<String> majorRegions = localAccidentService.getAllMajorRegions();
        List<String> minorRegions = localAccidentService.getAllMinorRegions(); 

        // ✨ 연도별 사고 건수 데이터 가져오기
        List<Object[]> yearlyAccidentData = localAccidentService.getYearlyAccidentCount();
        
        // ✨ 연도와 사고 건수를 따로 저장
        List<Integer> chartYears = yearlyAccidentData.stream().map(obj -> (Integer) obj[0]).toList();
        List<Long> chartCounts = yearlyAccidentData.stream().map(obj -> (Long) obj[1]).toList();

        model.addAttribute("accidents", pagedAccidents.getContent());
        model.addAttribute("paging", pagedAccidents);
        model.addAttribute("years", yearsList);
        model.addAttribute("majorRegions", majorRegions);
        model.addAttribute("minorRegions", minorRegions);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedMajorRegion", majorRegion);
        model.addAttribute("selectedMinorRegion", minorRegion);
        model.addAttribute("keyword", keyword);
        model.addAttribute("yearlyAccidentDataJson", new ObjectMapper().writeValueAsString(yearlyAccidentData));

        // ✨ 차트 데이터를 모델에 추가
        model.addAttribute("chartYears", chartYears);
        model.addAttribute("chartCounts", chartCounts);

        return "accidents/list";
    }

    /**
     * API: 선택한 주요 지역(시/도)에 따른 시/군/구 목록 반환
     */
    @GetMapping("/minorRegions")
    public ResponseEntity<List<String>> getMinorRegions(@RequestParam(value = "majorRegion", required = false) String majorRegion) {
        log.info(">>> Fetching minor regions for majorRegion: {}", majorRegion);

        List<String> minorRegions;
        if (majorRegion != null && !majorRegion.isEmpty()) {
            minorRegions = localAccidentRepository.findMinorRegionsByMajorRegion(majorRegion);
        } else {
            minorRegions = localAccidentRepository.findAllMinorRegions(); // 전체 조회
        }

        log.info(">>> Found {} minor regions", minorRegions.size());
        return ResponseEntity.ok(minorRegions);
    }

    /**
     * API: 필터링된 사고 데이터를 JSON으로 반환
     */
    @GetMapping("/list1/json")
    public ResponseEntity<List<LocalAccident>> getAccidentData(
        @RequestParam(value = "year", required = false) Integer year,
        @RequestParam(value = "majorRegion", required = false) String majorRegion,
        @RequestParam(value = "minorRegion", required = false) String minorRegion) {
        
        log.info(">>> Fetching accidents JSON - Year: {}, MajorRegion: {}, MinorRegion: {}", year, majorRegion, minorRegion);

        List<LocalAccident> accidents;
        if (year != null || (majorRegion != null && !majorRegion.isEmpty()) || (minorRegion != null && !minorRegion.isEmpty())) {
            accidents = localAccidentService.getAccidentsFiltered(year, majorRegion, minorRegion);
        } else {
            accidents = localAccidentService.getAllLocalAccidents();
        }

        log.info(">>> Returning {} accident records as JSON", accidents.size());
        return ResponseEntity.ok(accidents);
    }
       
}