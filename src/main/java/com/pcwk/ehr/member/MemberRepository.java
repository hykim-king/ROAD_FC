package com.pcwk.ehr.member;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface MemberRepository extends JpaRepository<Member,Long>{
	
	@Transactional
	@Modifying
	@Query(value = "delete from member where id in :ids", nativeQuery = true)
	void deleteByIdsInQuery(@Param("ids") List<Long> ids);
	
	@Transactional
    @Modifying
    @Query("UPDATE Member SET userGrade = 1 WHERE id IN (:ids)")
    void promoteToAdmin(@Param("ids") List<Long> ids);
	
	Page<Member> findAll(Specification<Member> spec, Pageable pageable);
	
	@Override
	Page<Member> findAll(Pageable pageable) ;
	
	Optional<Member> findByusername(String username);
	
	List<Member> findByUsername(String username);
	
	Optional<Member> findByEmail(String email);
}
