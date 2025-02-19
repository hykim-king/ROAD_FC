### 🗺 카카오맵 마커 클러스터러

![Image](https://github.com/user-attachments/assets/a0cbf188-c07c-4a60-a11a-c0dda139c4c9)

```
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


### 🗺 시구/시도 구분한 지도 폴리곤

![Image](https://github.com/user-attachments/assets/27ed5fae-6fdf-42a4-bf59-1d1b9939e352)


---


### 🛠 Tech Stacks
---
<img src = "https://img.shields.io/badge/HTML-239120?style=for-the-badge&logo=html5&logoColor=white"><img src = "https://img.shields.io/badge/CSS-239120?&style=for-the-badge&logo=css3&logoColor=white">

