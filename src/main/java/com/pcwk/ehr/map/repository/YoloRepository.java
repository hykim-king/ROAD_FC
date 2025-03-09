package com.pcwk.ehr.map.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pcwk.ehr.map.entity.Yolo;
import com.pcwk.ehr.map.entity.YoloInterface;

public interface YoloRepository extends JpaRepository<Yolo, Integer> {
	
	@Query("SELECT y.yoloObjectName AS yoloObjectName,\r\n"
			+ "       SUM(y.yoloObjectCount) AS yoloObjectCount,\r\n"
			+ "       AVG(y.yoloConfidence) AS yoloConfidence\r\n"
			+ "FROM Yolo y \r\n"
			+ "WHERE y.yoloImagePath LIKE %:folder%\r\n"
			+ "GROUP BY y.yoloObjectName")
	List<YoloInterface> findAverageData(@Param("folder") String folder);
    
}
