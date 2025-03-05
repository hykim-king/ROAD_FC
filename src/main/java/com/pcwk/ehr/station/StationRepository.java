package com.pcwk.ehr.station;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Integer> {

	List<Station> findAll();
}
