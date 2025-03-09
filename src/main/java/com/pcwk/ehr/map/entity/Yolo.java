package com.pcwk.ehr.map.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Table(name = "YOLO_DETECTIONS")
@ToString
public class Yolo {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer yoloId;
	
	@Column(length = 255 , nullable = false)
	private String yoloObjectName;
	
	@Column(columnDefinition = "NUMBER(4,3)", nullable = false)
	private double yoloConfidence;
	
	@Column(length = 255, nullable = false)
	private String yoloImagePath;
	
	@Column(columnDefinition = "NUMBER", nullable = false)
	private Integer yoloObjectCount;
	
	@Column(columnDefinition = "DATE DEFAULT CURRENT_DATE", nullable = false)
	private LocalDateTime yoloDetectionTime;
	
}
