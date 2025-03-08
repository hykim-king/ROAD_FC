package com.pcwk.ehr.map.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pcwk.ehr.freezing.Freezing;

public interface FreezingRepository extends JpaRepository<Freezing, Integer> {
	List<Freezing> findAll();
}