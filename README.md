### 🗺 카카오맵 마커 클러스터러
---

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=db6b66ee32de5c188c171ffea6d8fe47&libraries=services,clusterer"></script>

</head>
<body>
	<div id="map" style="width:100%;height:800px;"></div>
	
	<!-- script태그 안에있는 thymeleaf문 해석하려고 th:inline 사용 -->
	<script th:inline = "javascript">
	   var mapContainer = document.getElementById('map');
	   
       var mapOption = {
           center: new kakao.maps.LatLng(37.566826, 126.9786567), 
           level: 10
       };
	   
       
	   var map = new kakao.maps.Map(mapContainer, mapOption);
	   	   
	   /* 
	   사고 마커 이미지
	   var accidentImageSrc = "https://t1.daumcdn.net/localimg/localimages/07/2018/pc/trafficinfo/accident.png" 
	   공사 마커 이미지
	   var constructionImageSrc = "https://t1.daumcdn.net/localimg/localimages/07/2018/pc/trafficinfo/construction.png" 
	   */
	   
	   var cctvImageSrc = "https://t1.daumcdn.net/localimg/localimages/07/2023/pc/marker_cctv.png",
	   	   imageSize = new kakao.maps.Size(22,23);
	   
	   var markerImage = new kakao.maps.MarkerImage(cctvImageSrc, imageSize);
	   
	   /* 내츄럴 템플릿 (주석 지우면 X)*/
	   var cctvInfo = /*[[${cctvInfo}]]*/ [];
	   
	   var markers = [];

       cctvInfo.forEach(function(cctv) {
           var marker = new kakao.maps.Marker({
               position: new kakao.maps.LatLng(cctv.cctv_lat, cctv.cctv_lon),
               image: markerImage
           });
           markers.push(marker);
       });

       var clusterer = new kakao.maps.MarkerClusterer({
           map: map,
           markers: markers,
           gridSize: 50, // Number : 클러스터의 격자 크기. 화면 픽셀 단위이며 해당 격자 영역 안에 마커가 포함되면 클러스터에 포함시킨다 
           averageCenter: true, // Boolean : 마커들의 좌표 평균을 클러스터 좌표 설정 여부
           minLevel: 10, // Number : 클러스터링 할 지도의 최소 레벨 값
           disableClickZoom: true // Boolean : 클러스터 클릭 시 지도 확대 여부. true로 설정하면 클러스터 클릭 시 확대 되지 않는다
       });
	  
	</script>
	
</body>

</html>
```
<br>

### 🗺 시구/시도 구분한 지도 폴리곤
---
```html
<!DOCTYPE html>  
<html>  
<head>  
    <meta charset="utf-8">  
    <title>여러개 마커 제어하기</title>  
</head>  
<body>  
<div id="map" style="width:100%;height:800px;"></div>  
  
<script type="text/javascript" src="https://code.jquery.com/jquery-3.5.1.js"></script>  
<script type="text/javascript" src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=db6b66ee32de5c188c171ffea6d8fe47"></script>  
<script>  
    let mapContainer = document.getElementById('map'),  
        mapOption = {  
            center: new kakao.maps.LatLng(37.566826, 126.9786567), // 지도의 중심좌표  
            level: 12 // 지도의 확대 레벨  
        };  
  
    let map = new kakao.maps.Map(mapContainer, mapOption),  
        customOverlay = new kakao.maps.CustomOverlay({});  
  
    let detailMode = false;  
    let level = '';  
    let polygons = [];  
  
    init("/json/sido.json"); // 초기 시작  
  
    kakao.maps.event.addListener(map, 'zoom_changed', function () {  
        level = map.getLevel();  
        if (!detailMode && level <= 10) {  
            detailMode = true;  
            removePolygon();  
            init("/json/sig.json");  
        } else if (detailMode && level > 10) {  
            detailMode = false;  
            removePolygon();  
            init("/json/sido.json");  
        }  
    });  
  
    // 모든 폴리곤을 지우는 함수  
    function removePolygon() {  
        for (let i = 0; i < polygons.length; i++) {  
            polygons[i].setMap(null);  
        }  
        polygons = [];  
    }  
  
    // 폴리곤 생성  
    function init(path) {  
        let areas = [];  // 여기서 areas 배열 초기화  
  
        // JSON 파일 파싱  
        $.getJSON(path, function (geojson) {  
            var units = geojson.features;  
  
            $.each(units, function (index, unit) {  
                var coordinates = [];  
                var name = '';  
                var cd_location = '';  
                coordinates = unit.geometry.coordinates;  
                name = unit.properties.SIG_KOR_NM;  
                cd_location = unit.properties.SIG_CD;  
  
                var ob = {  
                    name: name,  
                    path: [],  
                    location: cd_location  
                };  
  
                $.each(coordinates[0], function (index, coordinate) {  
                    ob.path.push(new kakao.maps.LatLng(coordinate[1], coordinate[0]));  
                });  
  
                areas.push(ob);  
            });  
  
            // JSON 로드 완료 후 displayArea 호출  
            for (var i = 0, len = areas.length; i < len; i++) {  
                displayArea(areas[i]);  
            }  
        }).fail(function() {  
            console.error("JSON 파일 로드 실패: " + path);  
        });  
    }  
  
    function displayArea(area) {  
        var polygon = new kakao.maps.Polygon({  
            map: map,  
            path: area.path,  
            strokeWeight: 2,  
            strokeColor: '#004c80',  
            strokeOpacity: 0.8,  
            fillColor: '#fff',  
            fillOpacity: 0.7  
        });  
        polygons.push(polygon);  
  
        kakao.maps.event.addListener(polygon, 'mouseover', function (mouseEvent) {  
            polygon.setOptions({fillColor: '#09f'});  
            customOverlay.setContent('<div class="area">' + area.name + '</div>');  
            customOverlay.setPosition(mouseEvent.latLng);  
            customOverlay.setMap(map);  
        });  
  
        kakao.maps.event.addListener(polygon, 'mousemove', function (mouseEvent) {  
            customOverlay.setPosition(mouseEvent.latLng);  
        });  
  
        kakao.maps.event.addListener(polygon, 'mouseout', function () {  
            polygon.setOptions({fillColor: '#fff'});  
            customOverlay.setMap(null);  
        });  
  
        kakao.maps.event.addListener(polygon, 'click', function (mouseEvent) {  
            if (!detailMode) {  
                map.setLevel(10);  
                var latlng = mouseEvent.latLng;  
                map.panTo(latlng);  
            } else {  
                // 상세 모드에서 클릭 이벤트 처리  
                // callFunctionWithRegionCode(area.location);  
            }  
        });  
    }  
</script>  
</body>  
</html>
```

<br>

### 🛠 Tech Stacks
---
<br>
<img src = "https://img.shields.io/badge/HTML-239120?style=for-the-badge&logo=html5&logoColor=white">
