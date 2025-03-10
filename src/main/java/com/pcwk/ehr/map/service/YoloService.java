package com.pcwk.ehr.map.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pcwk.ehr.map.entity.Cctv;
import com.pcwk.ehr.map.entity.YoloInterface;
import com.pcwk.ehr.map.repository.CctvRepository;
import com.pcwk.ehr.map.repository.YoloRepository;

@Service
public class YoloService {
	
	@Autowired
	CctvRepository cctvRepository;
	
	@Autowired
	YoloRepository yoloRepository;
	
	public List<Cctv> findSomthing(){
		return cctvRepository.yoloSelectCctv("광교터널2");
	}
	
    public List<YoloInterface> getAverageByObjectName() {
        String latestFolder = getLatestCctvFolder();
       
        if(latestFolder != null) {
        	// 최신 폴더가 null이 아니면 함수실행
        	return yoloRepository.findAverageData(latestFolder);
        }else {
        	return new ArrayList<>();
        }
        
    }
	
    // 최신 폴더 찾기
    public String getLatestCctvFolder() {
        File fileDir = new File("D:\\JAP_20240909\\04_SPRING\\BOOT\\WORKSPACE\\project_oracle\\runs\\detect");
        // 파일 존재여부 / 디렉토리 확인
        if (!fileDir.exists() || !fileDir.isDirectory()) {
        	return null;
        }
        
        // listFiles: 파일 목록 가져오기
        // dir: 부모 디렉토리 / name: dir안에 있는 폴더
        // name.startsWith("cctv"): name이 cctv인지 / name.substring(4).matches("\\d+"): cctv뒤 숫자 확인
        File[] cctvFolders = fileDir.listFiles((dir, name) -> name.startsWith("cctv") && name.substring(4).matches("\\d+"));
        
        if (cctvFolders == null || cctvFolders.length == 0) {
        	return null;
        }
        
        // cctv 폴더를 스트림으로 변환 
        return Arrays.stream(cctvFolders)
        		// cctvFolders에 있는 파일명 중 4번째 배열인 cctv 파일명 뒤 숫자를 추출한 후 숫자로 변환하여 비교 후 큰 값
                .max(Comparator.comparingInt(file -> Integer.parseInt(file.getName().substring(4))))
                // .max()를 통해 큰 값을 string으로 반환
                .map(File::getName)
                // 없을 경우 null 처리
                .orElse(null);
    }
}
