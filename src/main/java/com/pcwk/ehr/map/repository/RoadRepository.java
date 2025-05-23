package com.pcwk.ehr.map.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pcwk.ehr.map.entity.Road;

public interface RoadRepository extends JpaRepository<Road, String> {
	
	@Query("SELECT r \r\n"
			+ "FROM Road r \r\n"
			+ "WHERE TO_DATE(r.road_end, 'YYYY\"년\" MM\"월\" DD\"일\" HH24\"시\" MI\"분\"') > SYSDATE "
			+ "AND r.road_type = 1")
	List<Road> findByAccident();
	
	
	@Query("SELECT r \r\n"
			+ "FROM Road r \r\n"
			+ "WHERE TO_DATE(r.road_end, 'YYYY\"년\" MM\"월\" DD\"일\" HH24\"시\" MI\"분\"') > SYSDATE "
			+ "AND r.road_type = 2")
	List<Road> findByConstruction();
	
}	
 