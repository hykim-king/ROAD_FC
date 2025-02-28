package com.pcwk.ehr.map.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pcwk.ehr.map.entity.Cctv;

public interface CctvRepository extends JpaRepository<Cctv, Integer> {
	
	@Query(value="SELECT * " + "FROM CCTV "
	+ "WHERE CCTV_NAME NOT LIKE '%터널%'", nativeQuery = true)
	List<Cctv> findCctv();
	
}	