window.moveToRegion = function(region) {

};

kakao.maps.load(function() {
	const accidentButton = document.getElementById('accidentBtn');
	const constructionButton = document.getElementById('constructionBtn');
	console.log(accidentButton);
	console.log(constructionButton);

	accidentButton.addEventListener('click', showAccidentMarkers);
	constructionButton.addEventListener('click', showConstructionMarkers);

	//---------------지도 세팅---------------------
	var mapContainer = document.getElementById('map');
	console.log(mapContainer);

	var mapOption = {
		center: new kakao.maps.LatLng(36.5, 127.5),
		level: 11
	};

	var map = new kakao.maps.Map(mapContainer, mapOption);
	map.setMinLevel(6);
	map.setMaxLevel(13);
	//---------------지도 세팅 끝---------------------

	// 지역별 좌표 및 줌 레벨 설정
	var regionData = {
		nationwide: { lat: 36.5, lng: 127.5, level: 12 }, // 전국
		seoul: { lat: 37.5665, lng: 126.9780, level: 10 }, // 수도권
		gangwon: { lat: 37.8855, lng: 127.7298, level: 10 }, // 강원권
		chungcheong: { lat: 36.6356, lng: 127.4912, level: 10 }, // 충청권
		jeolla: { lat: 35.7175, lng: 126.8931, level: 10 }, // 전라권
		gyeongsang: { lat: 35.2394, lng: 128.6924, level: 10 } // 경상권
	};

	// 🚀 버튼 클릭 시 해당 지역으로 이동하는 함수
	window.moveToRegion = function(region) {
		var data = regionData[region];
		var moveLatLng = new kakao.maps.LatLng(data.lat, data.lng);
		map.setLevel(data.level);
		map.setCenter(moveLatLng);
	};
	//---------------공통--------------------
	       
	//---------------CCTV 관련--------------------
	var currentCctvManager = null;
	var cctvMarkers = [];
	var cctvImageSrc = "https://t1.daumcdn.net/localimg/localimages/07/2023/pc/marker_cctv.png";	   
	var cctvMarkerImage = new kakao.maps.MarkerImage(cctvImageSrc, imageSize);
	//---------------CCTV 관련 끝--------------------

	var accidentImageSrc = "https://t1.daumcdn.net/localimg/localimages/07/2018/pc/trafficinfo/accident.png";

	var constructionImageSrc = "https://t1.daumcdn.net/localimg/localimages/07/2018/pc/trafficinfo/construction.png";

	var imageSize = new kakao.maps.Size(24, 21);

	var accidentMarkerImage = new kakao.maps.MarkerImage(accidentImageSrc, imageSize);

	var constructionMarkerImage = new kakao.maps.MarkerImage(constructionImageSrc, imageSize);

	// 내츄럴 템플릿 (주석 지우면 X)
	//var accidentList = JSON.parse(document.getElementById('accidentList').value);

	//var constructionList = JSON.parse(document.getElementById('constructionList').value);

	console.log(accidentList);
	console.log(constructionList);

	var markers = [];
	var accidentMarkers = [];
	var constructionMarkers = [];

	cctvInfo.forEach(function(cctv) {
		var marker = new kakao.maps.Marker({
			position: new kakao.maps.LatLng(cctv.cctv_lat, cctv.cctv_lon),
			image: markerImage
		});
		markers.push(marker);
	});

	// 마커 그룹핑 클래스
	class CctvMarkerGroup {
		constructor(markers) {
			this.markers = markers;
			this.center = this.calculateCenter();
			this.representativeMarker = null;
		}

		calculateCenter() {
			//마커 객체가 생성되어있지 않으면 return
			if (this.markers.length == 0) return null;

			// 위도 합, 경도 합 변수들을 선언
			let sumOfLats = 0;
			let sumOfLongs = 0;

			// 마커 일일이 순회하여 각 위도와 경도의 합을 계산    		   
			this.markers.forEach(marker => {
				sumOfLats += marker.cctv_lat;
				sumOfLongs += marker.cctv_lon;
			});

			// 위도의 합/마커 갯수, 경도 합/마커 갯수 를 계산하여,
			// 평균 위도와 평균 경도를 return
			return {
				lat: sumOfLats / this.markers.length, //위도 평균값
				lon: sumOfLongs / this.markers.length //경도 평균값
			}
		}
	}

	// 마커 지도에 표시 클래스
	class CctvMarkerManager {
		constructor(map, cctvInfo, cctvMarkerImage) {
			this.map = map; // 지도
			this.cctvInfo = cctvInfo; // cctv 정보 ()
			this.cctvMarkerImage = cctvMarkerImage; // 마커이미지    	

			this.visibleMarkers = []; // 실제 맵에서 보이는 마커
			this.groups = []; // 마커 그룹들을 가지고 있는 그룹
			this.content = "";
			this.infoWindow = null; // 정보창 객체 참조 저장
			this.overlay = null; // 오버레이 객체 참조 저장        
			this.cctvMarkers = []; //마커 객체들을 담는 배열        
			this.bindEvents();

			// 줌 레벨 설정 (MAX,MIN)
			this.MAP_LEVELS = {
				MAX_LEVEL: 13, // 가장 ZOOM OUT된 상태
				MIN_LEVEL: 6   // 가장 ZOOM IN된 상태    			   
			};

			// GROUPING_DISTANCE 미리 계산하여 저장
			this.GROUPING_DISTANCE_BY_LEVEL = {
				13: 0.5,     // 50km
				12: 0.4,     // 40km
				11: 0.2,     // 20km
				10: 0.1,     // 10km	
				9: 0.05,     // 5km
				8: 0.02,     // 1km
				7: 0.0001,   // 0.01km (10미터)  
				6: 0.0001    // 0.01km (10미터) 
			};
		}

		//cctvInfo(cctv정보 배열)을 순회하며 marker 생성
		createCctvMarkers() {
			if (this.cctvMarkers.length > 0) return;

			for (let i = 0; i < this.cctvInfo.length; i++) {
				const cctv = this.cctvInfo[i];
				const marker = new kakao.maps.Marker({
					position: new kakao.maps.LatLng(cctv.cctv_lat, cctv.cctv_lon),
					image: this.cctvMarkerImage,
					map: null
				});
				this.cctvMarkers.push(marker);
			}
		}

		// 마커 그룹화 함수 (줌 레벨에 따라 LEVEL 6,7일 때는 모든 개인 마커, else, 그룹 대표 마커)
		setGroups() {
			this.groups = []; // 그룹 초기화

			const currentLevel = this.map.getLevel(); // 현재 줌 레벨
			this.GROUPING_DISTANCE = this.GROUPING_DISTANCE_BY_LEVEL[currentLevel]; // 레벨 별 그룹화 거리 설정

			// 현재 보이는 영역만 처리 
			const bounds = this.map.getBounds();
			const sw = bounds.getSouthWest(); //현재 영역의 southwest
			const ne = bounds.getNorthEast(); //현재 영역의 northeast

			// 현재 보이는 영역 내의 마커만 필터링
			// 현재 보이는 화면의 sw(왼쪽 밑 구석 포인트), ne(오른쪽 위 구석 포인트)를 기준으로
			// cctvInfo 중에서 해당하는 cctv 객체들만 visibleCcctvInfo에 추가
			const visibleCctvInfo = this.cctvInfo.filter(cctv => {
				const lat = cctv.cctv_lat; //y축(위도)
				const lon = cctv.cctv_lon; //x축(경도)
				console.log('lat:' + lat + 'lon' + lon);
				console.log('sw.getSouthWest:' + sw.getSouthWest + 'ne.getNorthEast' + ne.getNorthEast);
				return lat >= sw.getLat() && lat <= ne.getLat() &&
					lon >= sw.getLng() && lon <= ne.getLng();
			});

			// 그리드 기반 클러스터링
			const gridMap = new Map();
			const gridSize = this.GROUPING_DISTANCE; //그리드 크기는 그룹화 거리만큼.

			// 마커를 그리드에 추가
			visibleCctvInfo.forEach(cctv => {
				// 각 마커의 그리드 좌표 계산 
				const gridX = Math.floor(cctv.cctv_lat / gridSize);
				const gridY = Math.floor(cctv.cctv_lon / gridSize);
				const gridKey = `${gridX},${gridY}`;

				if (!gridMap.has(gridKey)) {
					gridMap.set(gridKey, []);
				}
				gridMap.get(gridKey).push(cctv);
			});

			//처리된 그리드 추적 초기화
			const processed = new Set();

			// 그리드 맵을 순회해서 처리되지 않은 그리드 기본 그리드로 설정
			gridMap.forEach((markers, gridKey) => {
				if (processed.has(gridKey)) return;

				// 현재 그리드 처리
				const [gridX, gridY] = gridKey.split(',').map(Number);
				const group = [...markers];
				processed.add(gridKey);

				// 인접 그리드 검사 (8방향)
				for (let dx = -1; dx <= 1; dx++) {
					for (let dy = -1; dy <= 1; dy++) {
						if (dx === 0 && dy === 0) continue;

						const neighborKey = `${gridX + dx},${gridY + dy}`;
						if (!gridMap.has(neighborKey) || processed.has(neighborKey)) continue;

						// 거리 검사 후 병합
						const neighborMarkers = gridMap.get(neighborKey);
						const shouldMerge = this.shouldMergeGrids(group, neighborMarkers);

						if (shouldMerge) {
							group.push(...neighborMarkers);
							processed.add(neighborKey);
						}
					}
				}
				// 병합이 완료된 마커 배열을 groups 배열에 추가
				if (group.length > 0) {
					this.groups.push(new CctvMarkerGroup(group));
				}
			});
		}

		// 두 그리드가 병합되어야 하는지 검사
		shouldMergeGrids(group1, group2) {
			// 간단한 휴리스틱: 두 그룹의 중심점 간 거리 계산
			const center1 = this.calculateGroupCenter(group1);
			const center2 = this.calculateGroupCenter(group2);

			const distance = this.calcDistance(
				center1.lat, center1.lon,
				center2.lat, center2.lon
			);

			return distance <= this.GROUPING_DISTANCE;
		}

		// 그룹의 중심점 계산 (간단한 구현)
		calculateGroupCenter(markers) {
			let sumLat = 0, sumLon = 0;
			markers.forEach(marker => {
				sumLat += marker.cctv_lat;
				sumLon += marker.cctv_lon;
			});

			return {
				lat: sumLat / markers.length,
				lon: sumLon / markers.length
			};
		}

		// 점과 점 거리 계산 최적화 (Math.pow 대신 직접 곱셈)
		calcDistance(lat1, lon1, lat2, lon2) {
			const latDiff = lat2 - lat1;
			const lonDiff = lon2 - lon1;
			return Math.sqrt(latDiff * latDiff + lonDiff * lonDiff);
		}

		// 지도에 표시될 마커 로드하는 함수 (디바운스 처리 추가)
		updateMarkers() {
			// 이전 타이머가 있으면 취소
			if (this._updateTimer) {
				clearTimeout(this._updateTimer);
			}

			// 디바운스 처리 (50ms 지연)
			this._updateTimer = setTimeout(() => {
				this.setGroups();
				this.clearMarkers(); // 모든 마커 지도에서 제거

				const currentLevel = this.map.getLevel(); // 현재 줌 레벨 
				const bounds = this.map.getBounds(); // 현재 지도 영역

				// 그룹 캐싱을 위한 맵
				const markerCache = new Map();

				this.groups.forEach(group => {
					if (!group.center) return;

					// 그룹 중심점의 위치 객체 생성
					const centerPosition = new kakao.maps.LatLng(group.center.lat, group.center.lon);

					// 현재 지도 영역 밖의 그룹일 경우 return
					if (!bounds.contain(centerPosition)) return;

					// 줌 레벨에 따른 마커 표시
					if (currentLevel >= this.MAP_LEVELS.MAX_LEVEL - 1) {
						// 최대한 축소된 상태 - 그룹 마커만 표시
						this.addMarker(centerPosition, `${group.cctvMarkers.length}개의 CCTV`, null, group);
					} else if (currentLevel <= this.MAP_LEVELS.MIN_LEVEL + 1) {
						// 최대한 확대된 상태 - 개별 마커 표시
						group.cctvMarkers.forEach(marker => {
							const markerPosition = new kakao.maps.LatLng(marker.cctv_lat, marker.cctv_lon);
							this.addMarker(markerPosition, marker.cctv_name, marker, null);
						});
					} else {
						// 중간 줌 레벨 - 그룹 마커 표시
						this.addMarker(centerPosition, `${group.cctvMarkers.length}개의 CCTV`, null, group);
					}
				});

				this._updateTimer = null;
			}, 50);
		}

		// 마커 생성 함수 (객체 재사용 최적화)
		addMarker(position, title, cctvData, group) {
			const marker = new kakao.maps.Marker({
				position: position,
				image: this.cctvMarkerImage
			});

			// 마커 데이터 저장
			marker.cctvData = cctvData;
			marker.group = group;

			// 마커 클릭 이벤트 추가 (이벤트 위임 패턴 적용)
			kakao.maps.event.addListener(marker, 'click', () => {
				const currentLevel = this.map.getLevel();

				if (currentLevel <= this.MAP_LEVELS.MIN_LEVEL + 1) {
					this.showMarkerOverlay(marker, cctvData);
				} else {
					this.showMarkerInfo(marker, title);
				}
			});

			marker.setMap(this.map); // 지도에 마커 표시
			this.visibleMarkers.push(marker); // 표시된 마커 목록에 추가
		}

		// 모든 마커 제거 함수
		clearMarkers() {
			// 효율적인 제거를 위해 marker.setMap(null) 호출
			const length = this.visibleMarkers.length;
			for (let i = 0; i < length; i++) {
				this.visibleMarkers[i].setMap(null);
			}
			this.visibleMarkers = []; // 보이는 마커 빈 배열로 초기화

			// 남아있는 오버레이나 정보창 닫기
			if (this.infoWindow) {
				this.infoWindow.close();
			}

			if (this.overlay) {
				this.overlay.setMap(null);
			}
		}

		// 마커 info display 함수
		showMarkerInfo(marker, title) {
			if (this.infoWindow) {
				this.infoWindow.close();
			}

			if (this.overlay) {
				this.overlay.setMap(null);
			}

			this.infoWindow = new kakao.maps.InfoWindow({
				content: `<div style="padding:5px;">${title}</div>`
			});
			this.infoWindow.open(this.map, marker); // 정보창 띄우기
		}

		// 마커 오버레이 display 함수 (DOM 생성 최적화)
		showMarkerOverlay(marker, cctvData) {
			if (this.infoWindow) {
				this.infoWindow.close();
			}

			if (this.overlay) {
				this.overlay.setMap(null);
			}

			// DOM 요소 생성
			const overlayContent = document.createElement('div');
			overlayContent.className = "cctv";

			// 템플릿 리터럴 사용으로 간소화
			overlayContent.innerHTML = `
	        <div class="info"> 
	            <div class="title"> 
	                ${cctvData ? cctvData.cctv_name : '미확인 CCTV'} 
	                <div class="close" onclick="closeOverlay()" title="닫기">닫기</div>
	            </div>
	            <div class="body"> 
	                <div class="cctvVideo">
	                    <video id="cctv-video-player" autoplay muted width="320"></video>
	                </div> 
	                <div class="desc"> 
	                    <div class="ellipsis">${cctvData ? cctvData.address || '주소 정보 없음' : '위치 정보 없음'}</div> 
	                    <div class="jibun ellipsis">${cctvData ? cctvData.jibun || '' : ''}</div> 
	                    <div>${marker.getPosition().toString()}</div> 
	                </div> 
	            </div> 
	        </div>`;

			// 오버레이 생성 및 지도에 표시
			this.overlay = new kakao.maps.CustomOverlay({
				position: marker.getPosition(),
				content: overlayContent,
				map: this.map
			});
			setTimeout(() => {
				const videoElement = overlayContent.querySelector('#cctv-video-player');

				if (Hls.isSupported()) {
					var hls = new Hls();
					hls.loadSource(cctvData.cctv_url);
					hls.attachMedia(videoElement);
				}
				console.log(`cctvData.cctv_url: ${cctvData.cctv_url}`);
			}, 500);



			// 닫기 버튼 이벤트 처리
			const closeBtn = overlayContent.querySelector('.close');
			if (closeBtn) {
				closeBtn.onclick = () => {
					this.overlay.setMap(null);
				};
			}
		}

		// 지도 zoom level 변경, 이동 시 마커 업데이트 함수 (디바운스 처리)
		bindEvents() {
			// 이벤트 등록을 위한 헬퍼 함수 (디바운스 포함)
			const addEventWithDebounce = (eventName, delay) => {
				let timer = null;

				kakao.maps.event.addListener(this.map, eventName, () => {
					// activeMode가 'cctv'인 경우에만 처리
					if (window.activeMode !== 'cctv') return;

					// 기존 타이머 취소
					if (timer) clearTimeout(timer);

					// 새 타이머 설정
					timer = setTimeout(() => {
						this.updateMarkers();
						timer = null;
					}, delay);
				});
			};

			// 줌 이벤트 (더 긴 지연 적용)
			addEventWithDebounce('zoom_changed', 100);

			// 지도 이동 완료 이벤트 (idle)
			addEventWithDebounce('idle', 50);
		}
		// 전체 지도 업데이트 (공용 메서드)
		refreshMap() {
			this.clearMarkers();
			this.updateMarkers();
		}
	}

	// closeOverlay 전역 함수 정의 (오버레이 닫기 버튼용)
	function closeOverlay() {
		if (window.cctvMarkerManager && window.cctvMarkerManager.overlay) {
			window.cctvMarkerManager.overlay.setMap(null);
		}
	}

	const markerManager = new MarkerManager(map, cctvInfo, markerImage);

	// 마커 그룹핑 클래스
	class FreezingMarkerGroup {
		constructor(freezingMarkers) {
			this.freezingMarkers = freezingMarkers;
			this.center = this.calculateCenter();
			this.representativeMarker = null;
		} BG

		calculateCenter() {
			//마커 객체가 생성되어있지 않으면 return
			if (this.freezingMarkers.length == 0) return null;

			// 위도 합, 경도 합 변수들을 선언
			let sumOfLats = 0;
			let sumOfLongs = 0;

			// 마커 일일이 순회하여 각 위도와 경도의 합을 계산    		   
			this.freezingMarkers.forEach(marker => {
				sumOfLats += marker.freezingStartLat;
				sumOfLongs += marker.freezingStartLon;
			});

			// 위도의 합/마커 갯수, 경도 합/마커 갯수 를 계산하여,
			// 평균 위도와 평균 경도를 return
			return {
				lat: sumOfLats / this.freezingMarkers.length, //위도 평균값
				lon: sumOfLongs / this.freezingMarkers.length //경도 평균값
			}
		}
	}

	class FreezingMarkerManager {
		constructor(map, freezingInfo, freezingMarkerImage) {
			this.map = map;
			this.freezingInfo = freezingInfo;
			this.freezingMarkerImage = freezingMarkerImage;
			this.visibleMarkers = [];
			this.content = "";
			this.showFreezingMarkers();
			this.MAP_LEVELS = {
				MAX_LEVEL: 13, // 가장 ZOOM OUT된 상태
				MIN_LEVEL: 6   // 가장 ZOOM IN된 상태    			   
			};
			this.bindEvents();
		}

		showFreezingMarkers() {
			freezingInfo.forEach(function(freezing) {
				var marker = new kakao.maps.Marker({
					position: new kakao.maps.LatLng(freezing.freezingStartLat, freezing.freezingStartLon),
					image: freezingMarkerImage,
					map: null
				});
				freezingMarkers.push(marker);
			});
		}

		setGroups() {
			this.groups = []; // 마커 그룹들을 가지고 있는 그룹
			console.log("setGroups()")

			let ungroupedMarkers = [...this.freezingInfo];

			currentLevel = this.map.getLevel();
			this.setGroupingDistance(currentLevel);

			while (ungroupedMarkers.length > 0) {
				let currentMarker = ungroupedMarkers[0];
				let group = [currentMarker];

				for (let i = 1; i < ungroupedMarkers.length; i++) {
					let distance = this.calcDistance(ungroupedMarkers[i].freezingStartLat
						, ungroupedMarkers[i].freezingStartLon
						, currentMarker.freezingStartLat
						, currentMarker.freezingStartLon);

					if (distance <= this.GROUPING_DISTANCE) {
						group.push(ungroupedMarkers[i]);
					}
				}

				ungroupedMarkers = ungroupedMarkers.filter(marker =>
					!group.includes(marker)
				);

				this.groups.push(new FreezingMarkerGroup(group));
			}
		}

		calcDistance(lat1, lon1, lat2, lon2) {
			let distance = Math.sqrt(
				Math.pow(lat2 - lat1, 2) + Math.pow(lon2 - lon1, 2)
			);

			return distance;
		}

		setGroupingDistance(currentLevel) {
			const distanceByLevel = {
				13: 0.6,		// 50km
				12: 0.5,		// 40km
				11: 0.35,		// 20km
				10: 0.2,		// 10km	
				9: 0.1,		// 5km
				8: 0.06,		// 1km
				7: 0.0001,     // 0.01km (10미터)  
				6: 0.0001		// 0.01km (10미터) 
			};

			this.GROUPING_DISTANCE = distanceByLevel[currentLevel];
		}

		updateMarkers() {
			this.setGroups();
			this.clearMarkers();

			currentLevel = this.map.getLevel();
			const bounds = this.map.getBounds();

			console.log('currentLevel: ' + currentLevel);
			console.log('GroupingDistance: ' + this.GROUPING_DISTANCE);

			this.groups.forEach(group => {
				if (!group.center) return;

				const centerPosition = new kakao.maps.LatLng(group.center.lat, group.center.lon);

				if (!bounds.contain(centerPosition)) return;

				if (currentLevel == this.MAP_LEVELS.MAX_LEVEL) {
					this.addMarker(centerPosition, `${group.freezingMarkers.length}개의 결빙 취약 지점`);

				} else if (currentLevel == this.MAP_LEVELS.MIN_LEVEL || currentLevel == 7) {
					group.freezingMarkers.forEach(marker => {
						const markerPosition = new kakao.maps.LatLng(marker.freezingStartLat, marker.freezingStartLon);
						this.addMarker(markerPosition, marker.freezingAgency);
					});
				} else {

					this.addMarker(centerPosition, `${group.freezingMarkers.length}개의 결빙 취약 지점`);
				}
			});
		}

		addMarker(position, title) {
			const marker = new kakao.maps.Marker({
				position: position,
				image: this.freezingMarkerImage
			});

			kakao.maps.event.addListener(marker, 'click', () => {
				if (!(currentLevel == 6 || currentLevel == 7)) {
					this.showMarkerInfo(marker, title);
				} else {
					this.showMarkerOverlay(marker, overlayContent);
				}
			});

			marker.setMap(this.map);
			this.visibleMarkers.push(marker);
		}

		clearMarkers() {
			this.visibleMarkers.forEach(marker => marker.setMap(null));
			this.visibleMarkers = [];
		}

		showMarkerInfo(marker, title) {
			if (infoWindow != null) {
				infoWindow.close();
			}

			infoWindow = new kakao.maps.InfoWindow({
				content: `<div style = "padding:5px;">${title}</div>`
			});
			infoWindow.open(this.map, marker);
		}

		// 마커 오버레이 display 함수
		showMarkerOverlay(marker, overlayContent) {
			overlayContent =
				'<div class="wrap">' +
				'    <div class="info">' +
				'        <div class="title">' +
				'            카카오 스페이스닷원' +
				'            <div class="close" onclick="closeOverlay()" title="닫기">닫기</div>' +
				'        </div>' +
				'        <div class="body">' +
				'            <div class="img">' +
				'                <iframe width="560" height="315"src="https://www.youtube.com/embed/kamsx_g2hnI?loop=1&autoplay=1&mute=1&playlist=kamsx_g2hnI" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>' +
				'           </div>' +
				'            <div class="desc">' +
				'                <div class="ellipsis">제주특별자치도 제주시 첨단로 242</div>' +
				'                <div class="jibun ellipsis">(우) 63309 (지번) 영평동 2181</div>' +
				'                <div><a href="https://www.kakaocorp.com/main" target="_blank" class="link">홈페이지</a></div>' +
				'            </div>' +
				'        </div>' +
				'    </div>' +
				'</div>';

			overlay = new kakao.maps.CustomOverlay({
				position: marker.getPosition(),
				content: overlayContent,
				map: map
			});
		}

		bindEvents() {
			kakao.maps.event.addListener(this.map, 'zoom_changed', () => {
				if (activeMode === 'freezing') {
					this.updateMarkers();
					if (infoWindow != null) {
						infoWindow.close();
					}
				}
			});

			kakao.maps.event.addListener(this.map, 'idle', () => {
				if (activeMode === 'freezing') {
					this.updateMarkers();
				}
			});
		}
	}

	function cctvMode() {
		// 모드 상태 업데이트
		activeMode = 'cctv';
		// 결빙 마커 관리자가 존재한다면 모든 마커 제거
		if (currentFreezingManager) {
			currentFreezingManager.clearMarkers();
		}

		// 오버레이나 인포윈도우가 열려있다면 닫기
		if (overlay) overlay.setMap(null);
		if (infoWindow) infoWindow.close();

		// 모든 freezing 마커 직접 제거 (안전장치)
		freezingMarkers.forEach(function(marker) {
			marker.setMap(null);
		});
		freezingMarkers = []; // 배열 초기화

		// CCTV 마커 관리자가 없거나 새로 만들어야 한다면
		if (!currentCctvManager) {
			currentCctvManager = new CctvMarkerManager(map, cctvInfo, cctvMarkerImage);
		} else {
			// 기존 CCTV 마커 관리자가 있다면 마커 업데이트
			currentCctvManager.updateMarkers();
		}
	}

	function freezingMode() {
		// 모드 상태 업데이트
		activeMode = 'freezing';

		// CCTV 마커 관리자가 존재한다면 모든 마커 제거
		if (currentCctvManager) {
			currentCctvManager.clearMarkers();
		}

		// 오버레이나 인포윈도우가 열려있다면 닫기
		if (overlay) overlay.setMap(null);
		if (infoWindow) infoWindow.close();

		// 모든 CCTV 마커 직접 제거 (안전장치)
		cctvMarkers.forEach(function(marker) {
			marker.setMap(null);
		});
		cctvMarkers = []; // 배열 초기화

		// 결빙 마커 관리자가 없거나 새로 만들어야 한다면
		if (!currentFreezingManager) {
			currentFreezingManager = new FreezingMarkerManager(map, freezingInfo, freezingMarkerImage);
		} else {
			// 기존 결빙 마커 관리자가 있다면 마커 업데이트
			currentFreezingManager.updateMarkers();
		}
	}

	// 열려있는 오버레이 담을 변수
	var currentOverlay = null;

	// 사고 마커 생성 
	accidentList.forEach(function(accident) {
		var marker = new kakao.maps.Marker({
			position: new kakao.maps.LatLng(accident.road_lat, accident.road_lon),
			image: accidentMarkerImage,
			map: null
		});

		var overlayContent = document.createElement('div');
		overlayContent.className = 'wrap';
		overlayContent.innerHTML = `
	            <div class="info">
	                <div class="type">
	                    <div>사고</div>
	                    <div class="close" title="닫기"></div>
	                </div>
	                <div class="body">
	              	  	<div class="desc">
		                    <div class="title">${accident.road_title}</div>
		                    <div class="time">${accident.road_start}~${accident.road_end}</div>
		                    <div class="source">경찰청(UTIC)</div>
	                    </div>
	                </div>
	            </div>`;

		var overlay = new kakao.maps.CustomOverlay({
			position: new kakao.maps.LatLng(accident.road_lat, accident.road_lon),
			content: overlayContent,
			map: null
		});

		// 오버레이 닫는 함수
		overlayContent.querySelector(".close").addEventListener("click", function() {
			overlay.setMap(null);
			if (currentOverlay == overlay) {
				currentOverlay = null;
			}
		});

		kakao.maps.event.addListener(marker, 'click', function() {

			if (currentOverlay) {
				currentOverlay.setMap(null);
			}

			overlay.setMap(map);
			currentOverlay = overlay;

		});

		accidentMarkers.push(marker);
	});


	// 공사 마커 생성
	constructionList.forEach(function(construction) {
		var marker = new kakao.maps.Marker({
			position: new kakao.maps.LatLng(construction.road_lat, construction.road_lon),
			image: constructionMarkerImage,
			map: null
		});

		var overlayContent = document.createElement('div');
		overlayContent.className = 'wrap';
		overlayContent.innerHTML = `
	    	  <div class="info">
	      		  <div class="type">
	                  <div>공사</div>
	                  <div class="close" title="닫기"></div>
	              </div>
	              <div class="body">
	              	  <div class="desc">
		                  <div class="title">${construction.road_title}</div>
		                  <div class="time">${construction.road_start}~${construction.road_end}</div>
		                  <div class="source">경찰청(UTIC)</div>
	                  </div>
	              </div>
	          </div>`;

		var overlay = new kakao.maps.CustomOverlay({
			position: new kakao.maps.LatLng(construction.road_lat, construction.road_lon),
			content: overlayContent,
			map: null
		});

		// 오버레이 닫는 함수
		overlayContent.querySelector(".close").addEventListener("click", function() {
			overlay.setMap(null);
			if (currentOverlay == overlay) {
				currentOverlay = null;
			}
		});

		kakao.maps.event.addListener(marker, 'click', function() {

			if (currentOverlay) {
				currentOverlay.setMap(null);
			}

			overlay.setMap(map);
			currentOverlay = overlay;

		});

		constructionMarkers.push(marker);
	});

	// 사고 마커만 지도에 표시하는 함수
	function showAccidentMarkers() {
		// console.log(accidentButton + "click");
		if (currentOverlay) {
			currentOverlay.setMap(null);
			currentOverlay = null;
		}

		accidentMarkers.forEach(function(marker) {
			marker.setMap(map);
		});

		constructionMarkers.forEach(function(marker) {
			marker.setMap(null);
		});

	}

	// 공사 마커만 지도에 표시하는 함수
	function showConstructionMarkers() {
		// console.log(constructionButton + "click");
		if (currentOverlay) {
			currentOverlay.setMap(null);
			currentOverlay = null;
		}

		constructionMarkers.forEach(function(marker) {
			marker.setMap(map);
		});

		accidentMarkers.forEach(function(marker) {
			marker.setMap(null);
		});

	}

	map.addOverlayMapTypeId(kakao.maps.MapTypeId.TRAFFIC);

	// 버튼 클릭 이벤트 등록

});