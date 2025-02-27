package com.pcwk.ehr.map.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pcwk.ehr.map.entity.Cctv;

public interface CctvRepository extends JpaRepository<Cctv, Integer> {
	
	List<Cctv> findAll();
	
}	
