kakao.maps.load(function() {
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

	//-----------지역별 좌표 및 줌 레벨 설정-------------
	var regionData = {
		nationwide: { lat: 36.5, lng: 127.5, level: 12 }, // 전국
		seoul: { lat: 37.5665, lng: 126.9780, level: 10 }, // 수도권
		gangwon: { lat: 37.8855, lng: 127.7298, level: 10 }, // 강원권
		chungcheong: { lat: 36.6356, lng: 127.4912, level: 10 }, // 충청권
		jeolla: { lat: 35.7175, lng: 126.8931, level: 10 }, // 전라권
		gyeongsang: { lat: 35.2394, lng: 128.6924, level: 10 } // 경상권
	};

	//-------버튼 클릭 시 해당 지역으로 이동하는 함수---------
	window.moveToRegion = function(region) {
		var data = regionData[region];
		var moveLatLng = new kakao.maps.LatLng(data.lat, data.lng);
		map.setLevel(data.level);
		map.setCenter(moveLatLng);

		/*let safetyScore = safetyIndex.find(w =>
			w.weather_station_id && data
		)*/
	};
	//-----------지역별 좌표 및 줌 레벨 설정 끝-------------

	//---------------공통-----------------------------
	var activeMode = null;
	var infoWindow;
	var overlay;
	var currentLevel;
	var overlayContent;
	var currentOverlay = null;
	var imageSize = new kakao.maps.Size(24, 21);

	function closeOverlay() {
		overlay.setMap(null);
	}
	//---------------공통-----------------------------

	//---------------안전지수--------------------------
	var safetyIndexContent = [];
	//---------------안전지수--------------------------

	//---------------사고, 공사 관련--------------------
	var accidentMarkers = [];
	var constructionMarkers = [];
	var accidentImageSrc = "https://t1.daumcdn.net/localimg/localimages/07/2018/pc/trafficinfo/accident.png";
	var constructionImageSrc = "https://t1.daumcdn.net/localimg/localimages/07/2018/pc/trafficinfo/construction.png";
	var accidentMarkerImage = new kakao.maps.MarkerImage(accidentImageSrc, imageSize);
	var constructionMarkerImage = new kakao.maps.MarkerImage(constructionImageSrc, imageSize);
	//---------------사고, 공사 관련--------------------

	//---------------날씨 관련--------------------
	var weatherContent = [];
	//- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
	const skyImages = {
		"1": "/images/weather/sun.png",  // 맑음
		"3": "/images/weather/cloudy.png",  // 구름 많음
		"4": "/images/weather/blur.png"  // 흐림
	};

	//- 강수형태(PTY) 코드 : (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
	const ptyImages = {
		"0": null,  // 없음 (SKY 값 사용)
		"1": "/images/weather/rain.png",  // 비
		"2": "/images/weather/rainsnow.png",  // 비/눈
		"3": "/images/weather/snow.png",  // 눈
		"4": "/images/weather/shower.png"  // 소나기
	};
	//---------------날씨 관련--------------------

	//---------------CCTV 관련--------------------
	var currentCctvManager = null;
	var cctvMarkers = [];
	var cctvImageSrc = "https://t1.daumcdn.net/localimg/localimages/07/2023/pc/marker_cctv.png";
	var cctvMarkerImage = new kakao.maps.MarkerImage(cctvImageSrc, imageSize);
	//---------------CCTV 관련--------------------

	//---------------Freezing 관련--------------------
	var currentFreezingManager = null;
	var freezingMarkers = [];
	var freezingImageSrc = "https://www.roadplus.co.kr/gis/images/common/icn_blackice.png"; //결빙 이미지로 바꿔야함
	var freezingMarkerImage = new kakao.maps.MarkerImage(freezingImageSrc, imageSize);
	//---------------Freezing 관련--------------------

	//---------------터널 관련--------------------
	var currentTunnelManager = null;
	var tunnelMarkers = [];
	var tunnelImageSrc = "/images/tunnel/tunnel-icon.png";
	var tunnelMarkerImage = new kakao.maps.MarkerImage(tunnelImageSrc, imageSize);
	//---------------터널 관련--------------------

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

	// 날씨 생성
	findNowWeather.forEach(function(weather) {
		console.log("날씨 데이터:", weather);
		let imageUrl = null;
		let tempText = null;

		//category가 PTY고 0일 때 SKY이미지 사용
		if (weather.category === "PTY" && weather.fcstValue === "0") {

			let skyWeather = findNowWeather.find(w =>
				w.category === "SKY" &&
				w.weatherLat === weather.weatherLat &&
				w.weatherLon === weather.weatherLon
			);

			if (skyWeather) {
				imageUrl = skyImages[skyWeather.fcstValue];
			}
			//PTY가 0이 아닐 시 PTY이미지 사용
		} else if (weather.category === "PTY") {
			imageUrl = ptyImages[weather.fcstValue];
			//혹시 PTY데이터가 안들어왔을 때 대비
		} else if (weather.category === "SKY") {
			imageUrl = skyImages[weather.fcstValue];
		}

		//category가 TMP일 때 현재 weather와 같은 값 찾음
		let tempWeather = findNowWeather.find(w =>
			w.category === "TMP" &&
			w.weatherLat === weather.weatherLat &&
			w.weatherLon === weather.weatherLon
		);

		if (tempWeather) {
			tempText = `${tempWeather.fcstValue}°C`;
		}

		let regionName = findNowWeather.find(w =>
			w.regionName === weather.regionName &&
			w.weatherLat === weather.weatherLat &&
			w.weatherLon === weather.weatherLon
		);

		if (regionName) {
			regionText = `${regionName.regionName}`;
		}

		// 오버레이 컨텐츠 생성
		var content = document.createElement('div');
		content.className = 'weather-overlay';
		content.innerHTML = `
          <div class="weather-info">
              ${imageUrl ? `<img src="${imageUrl}" class="weather-icon">` : ''}
              <div class="weather-details">
                  ${tempText ? `<div class="temperature">${tempText}</div>` : ''}
                  ${regionText ? `<div class="location">${regionText}</div>` : ''}
              </div>
          </div>`;


		var overlay = new kakao.maps.CustomOverlay({
			position: new kakao.maps.LatLng(weather.weatherLat, weather.weatherLon),
			content: content,
			map: null
		});

		//console.log("선택된 이미지 URL:", imageUrl);
		weatherContent.push(overlay);
	});

	//cctv 마커들을 그룹화하기 위한 클래스 (zoom out할 때 대표 마커 설정)    
	class CctvMarkerGroup {
		constructor(cctvMarkers) {
			this.cctvMarkers = cctvMarkers;
			this.center = this.calculateCenter();
			this.representativeMarker = null;
		}
		//그룹 내 마커들의 위도와 경도의 평균값을 계산 후 중심점(위도,경도)를 return
		calculateCenter() {

			if (this.cctvMarkers.length === 0) return null;

			let sumOfLats = 0;
			let sumOfLongs = 0;

			// 마커 일일이 순회하여 각 위도와 경도의 합을 계산 (화살표 함수 대신 for문 사용)        
			const length = this.cctvMarkers.length;
			for (let i = 0; i < length; i++) {
				sumOfLats += this.cctvMarkers[i].cctv_lat;
				sumOfLongs += this.cctvMarkers[i].cctv_lon;
			}

			return {
				lat: sumOfLats / length, // 위도 평균값
				lon: sumOfLongs / length // 경도 평균값
			};
		}
	}

	//CCTV 마커들 지도에 배치를 관리하는 클래스
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
			console.log(`currentLevel: ${currentLevel}`);
			console.log(`this.GROUPING_DISTANCE: ${this.GROUPING_DISTANCE}`);

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
				const group = [...markers]; //마커 배열 복제 
				processed.add(gridKey);

				// 근처에 있는 그리드들을 검사 후 그룹핑 기준 거리보다 낮을 경우,
				// 병합. (상하좌우, 대각선 검사)
				for (let dx = -1; dx <= 1; dx++) { // x축 좌표 -1, 0, 1 = 왼쪽, 중간(기준 지점(현재 지점)), 오른쪽 포인트 
					for (let dy = -1; dy <= 1; dy++) { // y축 좌표 -1, 0, 1 = 아래, 중간(기준 지점(현재 지점)), 위
						if (dx === 0 && dy === 0) continue; // 기준점은 pass.

						const neighborKey = `${gridX + dx},${gridY + dy}`; //근처 그리드 좌표 (현재 그리드 좌표(gridX, gridY))에 각각 -1,0,1을 더해 인접 그리드의 좌표 계산.
						if (!gridMap.has(neighborKey) || processed.has(neighborKey)) continue; // 근처 그리드가 없거나, 그리드 안에 마커가 없으면 패스, 혹은 이미 처리된 그리드라면 패스.

						// 거리 검사 후 shouldMergeGrids()의 결과값으로 병합
						const neighborMarkers = gridMap.get(neighborKey); // 근처 그리드 안의 마커들 불러오기
						const shouldMerge = this.shouldMergeGrids(group, neighborMarkers); // 병합 여부 판단

						if (shouldMerge) {
							group.push(...neighborMarkers); // 현재 그룹(그리드)에 근처 그리드 안의 마커들 추가
							processed.add(neighborKey); // 그리드가 처리(병합)되었으니 처리완료 배열에 추가
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
		// 마커그룹1의 중간점, 마커그룹2의 중간점의 거리가 현재 레벨의 그룹핑 거리보다 적으면 병합 그리드 대상
		shouldMergeGrids(group1, group2) {

			const center1 = this.calculateGroupCenter(group1);
			const center2 = this.calculateGroupCenter(group2);

			const distance = this.calcDistance(
				center1.lat, center1.lon,
				center2.lat, center2.lon
			);

			return distance <= this.GROUPING_DISTANCE;
		}

		// markers안에 있는 모든 마커들의 위도와 경도의 평균 값을 구해서 중간 좌표 구하기
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

		// 좌표 2 곳의 위/경도의 차 구하기 (중간좌표 2곳 거리 구할 때 쓸 것)
		calcDistance(lat1, lon1, lat2, lon2) {
			const latDiff = lat2 - lat1;
			const lonDiff = lon2 - lon1;
			return Math.sqrt(latDiff * latDiff + lonDiff * lonDiff);
		}

		// 지도에 표시될 마커 로드하는 함수
		updateMarkers() {
			this.setGroups();
			this.clearMarkers(); // 모든 마커 지도에서 제거

			const currentLevel = this.map.getLevel(); // 현재 줌 레벨 
			const bounds = this.map.getBounds(); // 현재 지도 영역            

			this.groups.forEach(group => {
				if (!group.center) return;

				// 그룹 중심점의 위치 객체 생성
				const centerPosition = new kakao.maps.LatLng(group.center.lat, group.center.lon);

				// 현재 지도 영역 밖의 그룹일 경우 return
				if (!bounds.contain(centerPosition)) return;

				// 줌 레벨에 따른 마커 표시
				if (currentLevel <= this.MAP_LEVELS.MIN_LEVEL + 1) { // MIN LEVEL = 6, 레벨이 6이나 7일 때         
					group.cctvMarkers.forEach(marker => {
						const markerPosition = new kakao.maps.LatLng(marker.cctv_lat, marker.cctv_lon);
						this.addMarker(markerPosition, marker.cctv_name, marker, null);
					});
				} else {
					// 중간 줌 레벨 - 그룹 마커 표시
					this.addMarker(centerPosition, `${group.cctvMarkers.length}개의 CCTV`, null, group);
				}
			});
		}

		// 마커 생성 함수
		addMarker(position, title, cctvData, group) {
			const marker = new kakao.maps.Marker({
				position: position,
				image: this.cctvMarkerImage
			});

			// 마커 데이터 저장
			// 마커 클래스의 인스턴스 생성 후,
			// 동적으로 속성을 추가
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
			overlayContent.className = 'wrap';
			overlayContent.innerHTML = `
			      <div class="info">
							<div class="type">
								<div>CCTV</div>
								<div class="close" onclick="closeCctvOverlay()" title="닫기"></div>
							</div>
							<div class="body">
			          <div class="videoTitle">
			            ${cctvData.cctv_name}
			          </div>
			          <div class="videoContainer">
			            <video id="cctv-video-player" autoplay muted width="320"></video>
			          </div>
			          <div class="desc">
			            <div class="source">국가교통정보센터(LIVE) 제공</div>
			          </div>
			        </div>
			      </div>`;

			// 오버레이 생성 및 지도에 표시
			this.overlay = new kakao.maps.CustomOverlay({
				position: marker.getPosition(),
				content: overlayContent,
				map: this.map
			});

			const videoElement = overlayContent.querySelector('#cctv-video-player');

			if (Hls.isSupported()) {
				var hls = new Hls();
				hls.loadSource(cctvData.cctv_url);
				hls.attachMedia(videoElement);
			}

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
			// 지도 줌할 때 마커 재배치 
			kakao.maps.event.addListener(this.map, 'zoom_changed', () => {
				if (window.activeMode !== 'cctv') return;
				this.updateMarkers();
			});

			// 지도 이동할 때 마커 재배치 
			kakao.maps.event.addListener(this.map, 'idle', () => {
				if (window.activeMode !== 'cctv') return;
				this.updateMarkers();
			});
		}

		// 전체 지도 업데이트 (공용 메서드)
		refreshMap() {
			this.clearMarkers();
			this.updateMarkers();
		}
	}

	// closeOverlay 전역 함수 정의 (오버레이 닫기 버튼용)
	function closeCctvOverlay() {
		if (window.cctvMarkerManager && window.cctvMarkerManager.overlay) {
			window.cctvMarkerManager.overlay.setMap(null);
		}
	}

	// 결빙 마커 그룹화 클래스
	class FreezingMarkerGroup {
		constructor(freezingMarkers) {
			this.freezingMarkers = freezingMarkers; // 결빙 마커 배열
			this.center = this.calculateCenter();   // 그룹의 중심 좌표 계산         
		}

		// 중심 좌표 계산 메소드
		calculateCenter() {
			//마커 객체가 생성되어있지 않으면 return
			if (this.freezingMarkers.length == 0) return null; // 마커가 없을 때는 null return.

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

	// 결빙 마커들 지도에 배치를 관리하는 클래스
	class FreezingMarkerManager {
		constructor(map, freezingInfo, freezingMarkerImage) {
			this.map = map;                  //지도 객체
			this.freezingInfo = freezingInfo;        //결빙 데이터
			this.freezingMarkerImage = freezingMarkerImage;  //결빙 마커의 이미지         
			this.visibleMarkers = [];            //표시된 마커
			this.groups = [];                //마커 그룹

			this.MAP_LEVELS = {
				MAX_LEVEL: 13, // 가장 ZOOM OUT된 상태
				MIN_LEVEL: 6   // 가장 ZOOM IN된 상태            
			};
			this.bindEvents();    // 이벤트 탐지 후 마커 배치 함수(확대,축소,이동)
			this.updateMarkers(); // mode변경 시 초기 마커 띄우기(레벨에 따라)
		}

		setGroups() {
			this.groups = []; // 마커 그룹들을 가지고 있는 그룹
			console.log("setGroups()")

			let ungroupedMarkers = [...this.freezingInfo]; // 결빙 데이터 미그룹화 배열에 복사

			currentLevel = this.map.getLevel();     // 현재 줌 레벨
			this.setGroupingDistance(currentLevel); // 레벨마다 그룹화 기준 거리 설정

			while (ungroupedMarkers.length > 0) {
				let currentMarker = ungroupedMarkers[0];
				let group = [currentMarker];

				for (let i = 1; i < ungroupedMarkers.length; i++) {
					let distance = this.calcDistance(ungroupedMarkers[i].freezingStartLat
						, ungroupedMarkers[i].freezingStartLon
						, currentMarker.freezingStartLat
						, currentMarker.freezingStartLon);

					if (distance <= this.GROUPING_DISTANCE) {
						group.push(ungroupedMarkers[i]); // 기준 거리마다 그룹에 추가
					}
				}

				ungroupedMarkers = ungroupedMarkers.filter(marker =>
					!group.includes(marker)
				);

				this.groups.push(new FreezingMarkerGroup(group)); // 그룹 생성 후 추가                     
			}
		}

		// 두 좌표간 거리 계산
		calcDistance(lat1, lon1, lat2, lon2) {
			let distance = Math.sqrt(
				Math.pow(lat2 - lat1, 2) + Math.pow(lon2 - lon1, 2)
			);

			return distance;
		}

		// 줌 레벨에 따른 그룹화 기준 거리 설정       
		setGroupingDistance(currentLevel) {
			const distanceByLevel = {
				13: 0.6,    // 50km
				12: 0.5,    // 40km
				11: 0.35,   // 20km
				10: 0.2,    // 10km 
				9: 0.1,    // 5km
				8: 0.06,   // 1km
				7: 0.0001,     // 0.01km (10미터)  
				6: 0.0001    // 0.01km (10미터) 
			};
			this.GROUPING_DISTANCE = distanceByLevel[currentLevel];
		}

		// 마커 갱신 / 재배치 
		updateMarkers() {
			this.setGroups();    // 그룹화
			this.clearMarkers(); // 기존 마커 지우기

			currentLevel = this.map.getLevel();
			const bounds = this.map.getBounds();

			console.log('currentLevel: ' + currentLevel);
			console.log('GroupingDistance: ' + this.GROUPING_DISTANCE);

			this.groups.forEach(group => {
				if (!group.center) return;

				const centerPosition = new kakao.maps.LatLng(group.center.lat, group.center.lon);

				if (!bounds.contain(centerPosition)) return; // 현재 보고 있는 영역 밖의 그룹은 무시

				if (currentLevel <= this.MAP_LEVELS.MIN_LEVEL + 1) { // 레벨이 6이거나 7이면 (확대) 관리청 이름 표시
					group.freezingMarkers.forEach(marker => {
						const markerPosition = new kakao.maps.LatLng(marker.freezingStartLat, marker.freezingStartLon);
						this.addMarker(markerPosition, `관리청: ${marker.freezingAgency}`);
					});
				} else { // 아닐 때는 그룹 대표 마커 표시
					this.addMarker(centerPosition, `${group.freezingMarkers.length}개의 결빙 취약지점`);
				}
			});
		}

		// 마커 추가
		addMarker(position, title) {
			const marker = new kakao.maps.Marker({
				position: position,
				image: this.freezingMarkerImage
			});

			kakao.maps.event.addListener(marker, 'click', () => {
				this.showMarkerInfo(marker, title); // 클릭 시 infoWindow 표시
			});

			marker.setMap(this.map);
			this.visibleMarkers.push(marker);
		}

		// 기존 마커 제거 
		clearMarkers() {
			this.visibleMarkers.forEach(marker => {
				marker.setMap(null);
			});

			this.visibleMarkers = []; // 표시 마커 배열 초기화
		}

		// infoWindow(정보창) 표시
		showMarkerInfo(marker, title) {
			if (infoWindow != null) {
				infoWindow.close();
			}

			infoWindow = new kakao.maps.InfoWindow({
				content: `<div style = "padding:5px;">${title}</div>`
			});
			infoWindow.open(this.map, marker);
		}

		// 줌 IN, OUT하거나 영역 이동 시 마커 재배치 메소드
		bindEvents() {
			kakao.maps.event.addListener(this.map, 'zoom_changed', () => { // 줌 레벨 변경 시
				if (activeMode === 'freezing') {
					this.updateMarkers();
					if (infoWindow != null) {
						infoWindow.close();
					}
				}
			});

			kakao.maps.event.addListener(this.map, 'idle', () => { // 영역 이동 시
				if (activeMode === 'freezing') {
					this.updateMarkers();
					if (infoWindow != null) {
						infoWindow.close();
					}
				}
			});
		}
	}

	class TunnelMarkerGroup {
		constructor(tunnelMarkers) {
			this.tunnelMarkers = tunnelMarkers;
			this.center = this.calculateCenter();
			this.representativeMarker = null;
		}
		calculateCenter() {

			if (this.tunnelMarkers.length === 0) return null;

			let sumOfLats = 0;
			let sumOfLongs = 0;

			const length = this.tunnelMarkers.length;
			for (let i = 0; i < length; i++) {
				sumOfLats += this.tunnelMarkers[i].cctv_lat;
				sumOfLongs += this.tunnelMarkers[i].cctv_lon;
			}

			return {
				lat: sumOfLats / length,
				lon: sumOfLongs / length
			};
		}
	}

	class TunnelMarkerManager {
		constructor(map, tunnelInfo, tunnelMarkerImage) {
			this.map = map;
			this.tunnelInfo = tunnelInfo;
			this.tunnelMarkerImage = tunnelMarkerImage;

			this.visibleMarkers = [];
			this.groups = [];
			this.content = "";
			this.infoWindow = null;
			this.overlay = null;
			this.tunnelMarkers = [];
			this.bindEvents();

			this.MAP_LEVELS = {
				MAX_LEVEL: 13, // 가장 ZOOM OUT된 상태
				MIN_LEVEL: 6   // 가장 ZOOM IN된 상태             
			};

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

		createTunnelMarkers() {
			if (this.tunnelMarkers.length > 0) return;

			for (let i = 0; i < this.tunnelInfo.length; i++) {
				const tunnel = this.tunnelInfo[i];
				const marker = new kakao.maps.Marker({
					position: new kakao.maps.LatLng(tunnel.cctv_lat, tunnel.cctv_lon),
					image: this.tunnelMarkerImage,
					map: null
				});
				this.tunnelMarkers.push(marker);
			}
		}

		setGroups() {
			this.groups = [];

			const currentLevel = this.map.getLevel();
			this.GROUPING_DISTANCE = this.GROUPING_DISTANCE_BY_LEVEL[currentLevel];
			console.log(`currentLevel: ${currentLevel}`);
			console.log(`this.GROUPING_DISTANCE: ${this.GROUPING_DISTANCE}`);

			const bounds = this.map.getBounds();
			const sw = bounds.getSouthWest();
			const ne = bounds.getNorthEast();

			const visibleTunnelInfo = this.tunnelInfo.filter(tunnel => {
				const lat = tunnel.cctv_lat;
				const lon = tunnel.cctv_lon;

				return lat >= sw.getLat() && lat <= ne.getLat() &&
					lon >= sw.getLng() && lon <= ne.getLng();
			});

			const gridMap = new Map();
			const gridSize = this.GROUPING_DISTANCE;

			visibleTunnelInfo.forEach(tunnel => {
				const gridX = Math.floor(tunnel.cctv_lat / gridSize);
				const gridY = Math.floor(tunnel.cctv_lon / gridSize);
				const gridKey = `${gridX}, ${gridY}`;

				if (!gridMap.has(gridKey)) {
					gridMap.set(gridKey, []);
				}
				gridMap.get(gridKey).push(tunnel);
			});

			const processed = new Set();

			gridMap.forEach((markers, gridKey) => {
				if (processed.has(gridKey)) return;

				const [gridX, gridY] = gridKey.split(',').map(Number);
				const group = [...markers];
				processed.add(gridKey);

				for (let dx = -1; dx <= 1; dx++) {
					for (let dy = -1; dy <= 1; dy++) {
						if (dx === 0 && dy === 0) continue;

						const neighborKey = `${gridX + dx},${gridY + dy}`;
						if (!gridMap.has(neighborKey) || processed.has(neighborKey)) continue;

						const neighborMarkers = gridMap.get(neighborKey);
						const shouldMerge = this.shouldMergeGrids(group, neighborMarkers);

						if (shouldMerge) {
							group.push(...neighborMarkers);
							processed.add(neighborKey);
						}
					}
				}

				if (group.length > 0) {
					this.groups.push(new TunnelMarkerGroup(group));
				}
			});
		}

		shouldMergeGrids(group1, group2) {
			const center1 = this.calculateGroupCenter(group1);
			const center2 = this.calculateGroupCenter(group2);

			const distance = this.calcDistance(
				center1.lat, center1.lon,
				center2.lat, center2.lon
			);

			return distance <= this.GROUPING_DISTANCE;
		}

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

		calcDistance(lat1, lon1, lat2, lon2) {
			const latDiff = lat2 - lat1;
			const lonDiff = lon2 - lon1;
			return Math.sqrt(latDiff * latDiff + lonDiff * lonDiff);
		}

		updateMarkers() {
			this.createTunnelMarkers();
			this.setGroups();
			this.clearMarkers();

			const currentLevel = this.map.getLevel();
			const bounds = this.map.getBounds();

			this.groups.forEach(group => {
				if (!group.center) return;

				const centerPosition = new kakao.maps.LatLng(group.center.lat, group.center.lon);

				if (!bounds.contain(centerPosition)) return;

				if (currentLevel <= this.MAP_LEVELS.MIN_LEVEL + 1) {
					group.tunnelMarkers.forEach(marker => {
						const markerPosition = new kakao.maps.LatLng(marker.cctv_lat, marker.cctv_lon);
						this.addMarker(markerPosition, marker.cctv_name, marker, null);
					});
				} else {
					this.addMarker(centerPosition, `${group.tunnelMarkers.length}개의 터널`, null, group);
				}
			});
		}

		addMarker(position, title, tunnelData, group) {
			const marker = new kakao.maps.Marker({
				position: position,
				image: this.tunnelMarkerImage
			});

			marker.tunnelData = tunnelData;
			marker.group = group;

			kakao.maps.event.addListener(marker, 'click', () => {
				const currentLevel = this.map.getLevel();

				if (currentLevel <= this.MAP_LEVELS.MIN_LEVEL + 1) {
					this.showMarkerOverlay(marker, tunnelData);
				} else {
					this.showMarkerInfo(marker, title);
				}
			});

			marker.setMap(this.map);
			this.visibleMarkers.push(marker);
		}

		clearMarkers() {
			const length = this.visibleMarkers.length;
			for (let i = 0; i < length; i++) {
				this.visibleMarkers[i].setMap(null);
			}
			this.visibleMarkers = [];

			if (this.infoWindow) {
				this.infoWindow.close();
			}

			if (this.overlay) {
				this.overlay.setMap(null);
			}
		}

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
			this.infoWindow.open(this.map, marker);
		}

		showMarkerOverlay(marker, tunnelData) {
			if (this.infoWindow) {
				this.infoWindow.close();
			}

			if (this.overlay) {
				this.overlay.setMap(null);
			}

			const overlayContent = document.createElement('div');
			overlayContent.className = 'wrap';
			overlayContent.innerHTML = `
            <div class="info">
							<div class="type">
								<div>터널</div>
								<div class="close" onclick="closeTunnelOverlay()" title="닫기"></div>
							</div>
							<div class="body">
	              <div class="videoTitle">
	                ${tunnelData.cctv_name}
	              </div>
                <div class="videoContainer">
                  <video id="tunnel-video-player" autoplay muted width="320"></video>
                </div>
                <div class="desc">
                  <div class="source">국가교통정보센터(LIVE) 제공</div>
                </div>
              </div>
            </div>`;

			this.overlay = new kakao.maps.CustomOverlay({
				position: marker.getPosition(),
				content: overlayContent,
				map: this.map
			});

			const videoElement = overlayContent.querySelector('#tunnel-video-player');

			if (Hls.isSupported()) {
				var hls = new Hls();
				hls.loadSource(tunnelData.cctv_url);
				hls.attachMedia(videoElement);
			}

			const closeBtn = overlayContent.querySelector('.close');
			if (closeBtn) {
				closeBtn.onclick = () => {
					this.overlay.setMap(null);
				};
			}
		}

		bindEvents() {
			kakao.maps.event.addListener(this.map, 'zoom_changed', () => {
				if (window.activeMode !== 'tunnel') return;
				this.updateMarkers();
			});

			kakao.maps.event.addListener(this.map, 'idle', () => {
				if (window.activeMode !== 'tunnel') return;
				this.updateMarkers();
			});
		}

		refreshMap() {
			this.clearMarkers();
			this.updateMarkers();
		}
	}

	function closeTunnelOverlay() {
		if (window.tunnelMarkerManager && window.tunnelMarkerManager.overlay) {
			window.tunnelMarkerManager.overlay.setMap(null);
		}
	}

	// 마커 on/off 상태 관리 객체
	let activatedMarkers = {
		accident: false,
		construction: false,
		weather: false,
		cctv: false,
		freezing: false,
		tunnel: false
	};

	// 모든 마커를 숨기는 함수
	function hideAllMarkers(exceptType = null) {
		Object.keys(activatedMarkers).forEach(type => {
			if (type !== exceptType) {
				activatedMarkers[type] = false;
				setMarkers(type, false);
			}
		});
	}

	// 특정 타입의 마커를 on/off하는 함수
	function setMarkers(type, show) {
		if (currentOverlay) {
			currentOverlay.setMap(null);
			currentOverlay = null;
		}
		if (overlay) overlay.setMap(null);
		if (infoWindow) infoWindow.close();

		switch (type) {
			case "accident":
				accidentMarkers.forEach(marker => marker.setMap(show ? map : null));
				break;
			case "construction":
				constructionMarkers.forEach(marker => marker.setMap(show ? map : null));
				break;
			case "weather":
				weatherContent.forEach(marker => marker.setMap(show ? map : null));
				break;
			case "cctv":
				if (show) {
					if (!currentCctvManager) {
						currentCctvManager = new CctvMarkerManager(map, cctvInfo, cctvMarkerImage);
					}
					currentCctvManager.updateMarkers();
				} else {
					if (currentCctvManager) currentCctvManager.clearMarkers();
					cctvMarkers.forEach(marker => marker.setMap(null)); // 안전장치
				}
				break;
			case "freezing":
				if (show) {
					if (!currentFreezingManager) {
						currentFreezingManager = new FreezingMarkerManager(map, freezingInfo, freezingMarkerImage);
					}
					currentFreezingManager.updateMarkers();
				} else {
					if (currentFreezingManager) currentFreezingManager.clearMarkers();
					freezingMarkers.forEach(marker => marker.setMap(null));
				}
				break;
			case "tunnel":
				if (show) {
					if (!currentTunnelManager) {
						currentTunnelManager = new TunnelMarkerManager(map, tunnelInfo, tunnelMarkerImage);
					}
					currentTunnelManager.updateMarkers();
				} else {
					if (currentTunnelManager) currentTunnelManager.clearMarkers();
					tunnelMarkers.forEach(marker => marker.setMap(null));
				}
				break;
		}
	}

	// 줌 이벤트에서 활성화된 마커만 업데이트하도록 조건 강화
	kakao.maps.event.addListener(map, 'zoom_changed', function() {
		if (activatedMarkers.cctv && currentCctvManager) {
			console.log("🔍 줌 변경됨 - CCTV 마커 업데이트");
			currentCctvManager.updateMarkers();
		} else {
			console.log("❌ CCTV 마커는 활성화 상태가 아님");
		}

		if (activatedMarkers.freezing && currentFreezingManager) {
			console.log("🔍 줌 변경됨 - 결빙 마커 업데이트");
			currentFreezingManager.updateMarkers();
		} else {
			console.log("❌ 결빙 마커는 활성화 상태가 아님");
		}

		if (activatedMarkers.tunnel && currentTunnelManager) {
			console.log("🔍 줌 변경됨 - 터널 마커 업데이트");
			currentTunnelManager.updateMarkers();
		} else {
			console.log("❌ 터널 마커는 활성화 상태가 아님");
		}
	});

	kakao.maps.event.addListener(map, 'idle', function() {
		if (activatedMarkers.cctv && currentCctvManager) {
			console.log("🔍 줌 변경됨 - CCTV 마커 업데이트");
			currentCctvManager.updateMarkers();
		} else {
			console.log("❌ CCTV 마커는 활성화 상태가 아님");
		}

		if (activatedMarkers.freezing && currentFreezingManager) {
			console.log("🔍 줌 변경됨 - 결빙 마커 업데이트");
			currentFreezingManager.updateMarkers();
		} else {
			console.log("❌ 결빙 마커는 활성화 상태가 아님");
		}

		if (activatedMarkers.tunnel && currentTunnelManager) {
			console.log("🔍 줌 변경됨 - 터널 마커 업데이트");
			currentTunnelManager.updateMarkers();
		} else {
			console.log("❌ 터널 마커는 활성화 상태가 아님");
		}
	});

	function toggleMarkers(type) {
		const button = document.getElementById(`${type}Btn`);

		if (activatedMarkers[type]) {
			// 같은 버튼을 다시 클릭하면 마커 끄기
			activatedMarkers[type] = false;
			setMarkers(type, false);
			button.classList.remove("active"); // 버튼 비활성화 (색상 제거)
		} else {
			// 다른 버튼을 누르면 기존 마커 끄고 해당 마커 켜기
			hideAllMarkers(type);
			activatedMarkers[type] = true;
			setMarkers(type, true);

			// 모든 버튼의 active 클래스 제거 후 현재 버튼만 추가
			document.querySelectorAll(".typeBtn").forEach(btn => btn.classList.remove("active"));
			button.classList.add("active"); // 버튼 활성화 (색상 변경)
		}
	}

	// 버튼 이벤트 등록
	document.getElementById("accidentBtn").addEventListener("click", () => toggleMarkers("accident"));
	document.getElementById("constructionBtn").addEventListener("click", () => toggleMarkers("construction"));
	document.getElementById("weatherBtn").addEventListener("click", () => toggleMarkers("weather"));
	document.getElementById("cctvBtn").addEventListener("click", () => toggleMarkers("cctv"));
	document.getElementById("freezingBtn").addEventListener("click", () => toggleMarkers("freezing"));
	document.getElementById("tunnelBtn").addEventListener("click", () => toggleMarkers("tunnel"));

	map.addOverlayMapTypeId(kakao.maps.MapTypeId.TRAFFIC);
});