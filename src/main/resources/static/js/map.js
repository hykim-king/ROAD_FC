//---------------지도 세팅---------------------
var mapContainer = document.getElementById('map');

var mapOption = {
	center: new kakao.maps.LatLng(37.566826, 126.9786567),
	level: 10
};

var map = new kakao.maps.Map(mapContainer, mapOption);
map.setMinLevel(6);
map.setMaxLevel(13);
//---------------지도 세팅---------------------

//---------------공통--------------------	 
var infoWindow;
var overlay;
var currentLevel;
var overlayContent;
var imageSize = new kakao.maps.Size(22, 23);

function closeOverlay() {
	overlay.setMap(null);
}

//---------------공통--------------------

//---------------CCTV 관련--------------------
var currentCctvManager = null;
var cctvMarkers = [];
var cctvImageSrc = "https://t1.daumcdn.net/localimg/localimages/07/2023/pc/marker_cctv.png";
var cctvMarkerImage = new kakao.maps.MarkerImage(cctvImageSrc, imageSize);
/* 내츄럴 템플릿 (주석 지우면 X)*/
var cctvInfo = /*[[${cctvInfo}]]*/[];
//---------------CCTV 관련--------------------

//---------------Freezing 관련--------------------
var currentFreezingManager = null;
var freezingMarkers = [];
var freezingImageSrc = "/img/freezing-icon.png"; //결빙 이미지로 바꿔야함
var freezingMarkerImage = new kakao.maps.MarkerImage(freezingImageSrc, imageSize);
var freezingInfo = /*[[${freezingInfo}]]*/[];
//---------------Freezing 관련--------------------

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
		//overlayContent.className = "cctv";

		// 템플릿 리터럴 사용으로 간소화
		overlayContent.innerHTML = `
        <div class="info"> 
            <div class="title"> 
                ${cctvData.cctv_name}                 
            </div>
            <div class="body"> 
                <div class="cctvVideo">
                    <video id="cctv-video-player" autoplay muted width="320"></video>
                </div> 
                <div class="desc">                                           
                	<p>국가교통정보센터(LIVE) 제공</p>
                    <div class="close" onclick="closeOverlay()" title="닫기">닫기</div> 
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
function closeOverlay() {
	if (window.cctvMarkerManager && window.cctvMarkerManager.overlay) {
		window.cctvMarkerManager.overlay.setMap(null);
	}
}

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
		if (this.infoWindow) {
			this.infoWindow.close();
		}

		if (this.overlay) {
			this.overlay.setMap(null);
		}

		// DOM 요소 생성	   	        
		//overlayContent.className = "freezing";

		// 템플릿 리터럴 사용으로 간소화
		overlayContent.innerHTML = `
	   	        <div class="info"> 
	   	            <div class="title"> 
	   	                                 
	   	            </div>
	   	            <div class="body"> 
	   	                <div class="cctvVideo">
	   	                    
	   	                </div> 
	   	                <div class="desc">                                           
	   	                	<p>국가교통정보센터(LIVE) 제공</p>
	   	                    <div class="close" onclick="closeOverlay()" title="닫기">닫기</div> 
	   	                </div> 
	   	            </div> 
	   	        </div>`;

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