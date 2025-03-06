package com.pcwk.ehr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PaginationUtil {

	/**
	 * 📌 페이징 객체(Pageable) 생성 (정렬 기능 추가)
	 * 
	 * @param page      페이지 번호 (0부터 시작)
	 * @param size      페이지 크기 (한 페이지당 데이터 개수)
	 * @param sortBy    정렬 기준 필드 (예: "id", "createdAt" 등)
	 * @param ascending 오름차순(true) / 내림차순(false)
	 * @return Pageable 객체 반환
	 */
	public static Pageable createPageable(int page, int size) {
		return PageRequest.of(page, size);
	}

	/**
	 * 📌 Page 객체를 JSON 형식으로 변환 (응답 데이터 통일)
	 * 
	 * @param pageData 페이징된 데이터 (Page 객체)
	 * @return Map<String, Object> JSON 응답 형식
	 */
	public static Map<String, Object> createPagedResponse(Page<?> pageData) {
		Map<String, Object> response = new HashMap<>();
		response.put("content", pageData.getContent()); // 실제 데이터 목록
		response.put("currentPage", pageData.getNumber()); // 현재 페이지 번호
		response.put("totalPages", pageData.getTotalPages()); // 전체 페이지 수
		response.put("totalElements", pageData.getTotalElements()); // 전체 데이터 수
		response.put("size", pageData.getSize()); // 한 페이지당 개수
		response.put("hasNext", pageData.hasNext()); // 다음 페이지 여부
		response.put("hasPrevious", pageData.hasPrevious()); // 이전 페이지 여부
		response.put("isFirst", pageData.isFirst()); // 첫 페이지 여부
		response.put("isLast", pageData.isLast()); // 마지막 페이지 여부
		return response;
	}
}
