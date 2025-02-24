package com.pcwk.ehr.map.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pcwk.ehr.map.Entity.Cctv;

public interface CctvRepository extends JpaRepository<Cctv, Integer> {
	
	List<Cctv> findAll();
	

}	
