package com.pcwk.ehr.map.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pcwk.ehr.map.entity.Freezing;

@Repository
public interface FreezingRepository extends JpaRepository<Freezing, Integer> {
	
	List<Freezing> findAll();
}