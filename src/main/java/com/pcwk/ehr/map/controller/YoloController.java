package com.pcwk.ehr.map.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pcwk.ehr.map.entity.Cctv;
import com.pcwk.ehr.map.entity.YoloInterface;
import com.pcwk.ehr.map.service.YoloService;
import com.pcwk.ehr.member.Member;
import com.pcwk.ehr.member.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/yolo")
@Slf4j
public class YoloController {

	@Autowired
	YoloService yoloService;
	
    @Autowired
    MemberService memberService;
	
	@GetMapping("/detection")
	public String yoloDetection(Model model,
			HttpServletRequest request, Principal principal) {
		log.info("┌──────────────────┐");
		log.info("│ yoloDetection()  │");
		log.info("└──────────────────┘");	
		List<Cctv> yoloCctv = yoloService.findSomthing();
		log.info("yoloCctv:{}", yoloCctv);
		model.addAttribute("yoloCctv", yoloCctv);
		
		List<YoloInterface> yoloAvgData = yoloService.getAverageByObjectName();
		for (YoloInterface yo : yoloAvgData) {
		    log.info("ObjectName: " + yo.getYoloObjectName() + ", Confidence: " + yo.getYoloConfidence()
		    		+ ", YoloObjectCount: " + yo.getYoloObjectCount());
		}
		
		String username = principal.getName();
        Member member = memberService.getMember(username); // member 객체 가져오기
        model.addAttribute("member", member); // 모델에 member 추가
		
		model.addAttribute("yoloAvgData", yoloAvgData);
		model.addAttribute("page", "yolo");
		model.addAttribute("currentUrl", request.getRequestURI());
		
		return "map/yolo";
	}
	
	@PostMapping("/run-yolo")
	@ResponseBody
	public String runYolo(@RequestParam("cctv_url") String cctv_url) {
		try {
			String pythonPath = "C:\\Users\\acorn4\\AppData\\Local\\Programs\\Python\\Python312\\python.exe";
			String pythonScriptPath = "D:\\JAP_20240909\\04_SPRING\\BOOT\\WORKSPACE\\yolo\\cctv_detect.py";
			
			// 터미널 입력값 ex: python ttest_detect.py --source cctvUrl
			ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath, "--source" , cctv_url);
			// 실행 로그
			processBuilder.redirectErrorStream(true);
			processBuilder.inheritIO();
			Process process = processBuilder.start();
			// python 파일이 끝날 때까지 대기
			process.waitFor();
			return "yolo 분석 완료!";
		}catch (Exception e) {
			e.printStackTrace();
			return "yolo 안됨:" + e.getMessage();
		}
	}
}