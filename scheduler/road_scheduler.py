from concurrent.futures import ThreadPoolExecutor
import json
import urllib.request
import cx_Oracle as cx
from datetime import datetime
import ssl
import math
import schedule
import time
from urllib.request import urlopen
import urllib
import xmltodict
import oracledb
import pandas as pd
import requests
import io
import csv
from bs4 import BeautifulSoup

# === 기상청 격자 좌표를 위도/경도로 변환하는 코드 (제공해주신 코드) ===
NX = 149  # X축 격자점 수
NY = 253  # Y축 격자점 수

Re = 6371.00877  # 지도반경 (km)
grid = 5.0  # 격자간격 (km)
slat1 = 30.0  # 표준위도 1
slat2 = 60.0  # 표준위도 2
olon = 126.0  # 기준점 경도
olat = 38.0  # 기준점 위도
xo = 210 / grid  # 기준점 X좌표
yo = 675 / grid  # 기준점 Y좌표
first = 0

if first == 0:
    PI = math.asin(1.0) * 2.0
    DEGRAD = PI / 180.0
    RADDEG = 180.0 / PI

    re = Re / grid
    slat1 = slat1 * DEGRAD
    slat2 = slat2 * DEGRAD
    olon = olon * DEGRAD
    olat = olat * DEGRAD

    sn = math.tan(PI * 0.25 + slat2 * 0.5) / math.tan(PI * 0.25 + slat1 * 0.5)
    sn = math.log(math.cos(slat1) / math.cos(slat2)) / math.log(sn)
    sf = math.tan(PI * 0.25 + slat1 * 0.5)
    sf = math.pow(sf, sn) * math.cos(slat1) / sn
    ro = math.tan(PI * 0.25 + olat * 0.5)
    ro = re * sf / math.pow(ro, sn)
    first = 1

# nx,ny를 위도, 경도로 변환하는 함수
def gridToMap(x, y, code=1):
    # Oracle에 저장된 nx, ny 값 사용 (격자 좌표는 정수값)
    x = x - 1
    y = y - 1
    xn = x - xo
    yn = ro - y + yo
    ra = math.sqrt(xn * xn + yn * yn)
    if sn < 0.0:
        ra = -ra
    alat = math.pow((re * sf / ra), (1.0 / sn))
    alat = 2.0 * math.atan(alat) - PI * 0.5
    if abs(xn) <= 0.0:
        theta = 0.0
    else:
        if abs(yn) <= 0.0:
            theta = PI * 0.5
            if xn < 0.0:
                theta = -theta
        else:
            theta = math.atan2(xn, yn)
    alon = theta / sn + olon
    lat = alat * RADDEG
    lon = alon * RADDEG
    return lat, lon

# 지역명 지정하는 함수
def get_manual_region_name(lat, lon):

    location_map = {
        (37.579871128849334, 126.98935225645432): "서울",
        (35.5501698408776, 129.327820529466): "울산",
        (36.652179698279255, 127.49134891977629): "충북",
        (37.44396728824339, 126.69686463511782): "인천",
        (37.892508968399696, 127.75465283445429): "강원특별자치도",
        (36.470608394908616, 127.31553401415532): "세종특별자치시",
        (35.24836009019053, 128.69462181471292): "경남",
        (35.829433675439695, 127.13264104608315): "전북특별자치도",
        (35.190980143372386, 129.0850612696198): "부산",
        (35.14428133455586, 126.84058771184152): "광주",
        (36.58543021777893, 128.52147222317694): "경북",
        (35.8474679567114, 128.60617151756293): "대구",
        (36.33190094642055, 127.36978502691296): "대전",
        (37.2571760965906, 126.9843443901392): "경기",
        (34.82626173075432, 126.4461395091976): "전남",
        (33.5012420333313, 126.49196898149866): "제주특별자치도"
    }

    if (lat, lon) in location_map:
        return location_map[(lat, lon)]

# 기상청 단기예보 API 호출 후 좌표 가져오는 함수
def fetch_weather(nx_val, ny_val):
    base_url = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"
    service_key = "EAeXDDghH3ZXiqRdl5n3OXlCDOnL80NyAOLV8xJyWBquRYVHZczeG1qcGy4%2FPCaPK6q5M2Maq7uh9etBckzzOw%3D%3D"
    today = datetime.now().strftime("%Y%m%d")
    base_time = "0500"

    url = (
        f"{base_url}?serviceKey={service_key}"
        f"&pageNo=1&numOfRows=1000&dataType=JSON"
        f"&base_date={today}&base_time={base_time}"
        f"&nx={nx_val}&ny={ny_val}"
    )

    try:
        ssl_context = ssl.create_default_context()
        ssl_context.minimum_version = ssl.TLSVersion.TLSv1_2
        ssl_context.set_ciphers("DEFAULT:@SECLEVEL=1")
        weather_data = urllib.request.urlopen(url, context=ssl_context).read().decode('utf-8')
        data = json.loads(weather_data)
        print(f"도로 날씨 API 호출 성공 (nx={nx_val}, ny={ny_val})")
        return data
    except Exception as e:
        print(f"도로 날씨 API 요청 실패 (nx={nx_val}, ny={ny_val}):", e)
        return None

# 날씨 데이터 DB에 저장하는 함수
def road_weather_list():
    now = datetime.now()
    print(f"[도로 날씨 데이터] 배치 실행:{now.strftime('%Y-%m-%d %H:%M:%S')}")

    conn = get_db_connection()
    if not conn:
        return

    cursor = conn.cursor()

    # 기존 데이터 삭제
    cursor.execute("TRUNCATE TABLE ROAD_WEATHER")
    print("기존 도로 날씨 데이터 삭제")

    grid_coords = [
        (60, 127),(98, 76),(89, 90),(55, 124),(58, 74),(67, 100),
        (102, 84),(66, 103),(60, 120),(69, 107),(51, 67),(87, 106),
        (91, 77),(52, 38),(73, 134),(63, 89)
    ]

    insert_sql = """
    INSERT INTO ROAD_WEATHER 
      (BASE_DATE, BASE_TIME, CATEGORY, FCST_DATE, FCST_TIME, FCST_VALUE, NX, NY, REGION_NAME, WEATHER_LAT, WEATHER_LON, WEATHER_REG_DT)
    VALUES 
      (:1, :2, :3, :4, :5, :6, :7, :8, :9, :10, :11, :12)
    """

    # ThreadPoolExecutor: 비동기 실행
    # 최대 5개까지 실행
    with ThreadPoolExecutor(max_workers=5) as executor:
        # executor.map() -> grid_coords 에서 5개의 x,y 좌표를 가져옴 -> lambda coords에 전달 -> fetch_weather() 에 전달
        # coords 개별값을 주기 위해 언패킹(*)함 / 언패킹 안하면 튜플 전체 전달 ex: ((60, 127))
        # grid_coords에 있는 값만 사용하기 위해 list()를 사용해서 전체 값 출력
        results = list(executor.map(lambda coords: fetch_weather(*coords), grid_coords))

    if not results:
        print("도로 날씨 데이터 없음")
        return

    try:
        # executemany로 처리하기 위해 여러 개의 데이터를 담을 배열
        batch_data = []

        for data in results:
            for weather in data['response']['body']['items']['item']:
                if weather['category'] not in ('TMP', 'SKY', 'PTY'):
                    continue
                nx_int = int(weather['nx'])
                ny_int = int(weather['ny'])
                lat, lon = gridToMap(nx_int, ny_int)
                region_name = get_manual_region_name(lat, lon)

                values = (
                    weather['baseDate'],
                    weather['baseTime'],
                    weather['category'],
                    weather['fcstDate'],
                    weather['fcstTime'],
                    weather['fcstValue'],
                    nx_int,
                    ny_int,
                    region_name,
                    lat,
                    lon,
                    datetime.now()
                )
                # 각 데이터 batch_data에 넣기
                batch_data.append(values)

        if batch_data:
            # 데이터 하나 삽입할 때마다 커밋하면 많은 시간 소모
            # executemany(): 다중 행을 처리함
            cursor.executemany(insert_sql, batch_data)
            conn.commit()

        print("ROAD_WEATHER 데이터 삽입 완료!")

    except Exception as e:
        print("DB 입력 중 오류 발생:", e)

    finally:
        cursor.close()
        conn.close()

#----------------------------------------------------------------------------

# 도로교통센터 API 호출 후 데이터 가져오는 함수
def fetch_road_data():
    url = "https://www.utic.go.kr/guide/imsOpenData.do?key=2C3IfUVbmTRnJ5r29Z0Q3SO9EhGrxS5CGH0vRDb9o0"

    try:
        request = urllib.request.Request(url)
        response = urlopen(request).read()
        decode_data = response.decode('utf-8')
        xml_parse = xmltodict.parse(decode_data)
        xml_dict = json.loads(json.dumps(xml_parse))

        print("도로 상황 API 호출 성공")
        return xml_dict['result']['record']
    except Exception as e:
        print("도로 상황 API 요청 실패:", e)
        return None

# 도로 데이터를 DB에 저장하는 함수
def road_list():
    now = datetime.now()
    print(f"[도로 데이터] 배치 실행:{now.strftime('%Y-%m-%d %H:%M:%S')}")

    dsn = cx.makedsn('192.168.100.30', '1522', service_name='xe')
    try:
        conn = cx.connect(user='ROMA', password='roma1235', dsn=dsn, encoding='utf-8', nencoding='utf-8')
        print("DB 연결 성공")
    except Exception as e:
        print("DB 연결 실패:", e)
        return

    cursor = conn.cursor()
    cursor.execute("DELETE FROM ROAD WHERE TO_CHAR(TO_DATE(ROAD_END, 'YYYY\"년\" MM\"월\" DD\"일\" HH24\"시\" MI\"분\"'), 'YYYY-MM-DD') < TO_CHAR(SYSDATE, 'YYYY-MM-DD')")

    with ThreadPoolExecutor(max_workers=20) as executor:
        future = executor.submit(fetch_road_data)
        road_records = future.result()

    insert_sql = """
      MERGE INTO ROAD r
      USING (SELECT :1 AS ROAD_ID, :2 AS ROAD_ADDR, :3 AS ROAD_END, :4 AS ROAD_LAT, :5 AS ROAD_LON,
                    :6 AS ROAD_REG_DT, :7 AS ROAD_START, :8 AS ROAD_TITLE, :9 AS ROAD_TYPE, :10 AS ROAD_UPDATE
             FROM DUAL) rr
      ON (r.ROAD_ID = rr.ROAD_ID)
      WHEN MATCHED THEN
          UPDATE SET 
              r.ROAD_END = rr.ROAD_END, 
              r.ROAD_START = rr.ROAD_START, 
              r.ROAD_UPDATE = rr.ROAD_UPDATE
          WHERE r.ROAD_END <> rr.ROAD_END 
             OR r.ROAD_START <> rr.ROAD_START 
             OR r.ROAD_UPDATE <> rr.ROAD_UPDATE
      WHEN NOT MATCHED THEN
          INSERT (r.ROAD_ID, r.ROAD_ADDR, r.ROAD_END, r.ROAD_LAT, r.ROAD_LON, r.ROAD_REG_DT, r.ROAD_START, r.ROAD_TITLE, r.ROAD_TYPE, r.ROAD_UPDATE)
          VALUES (rr.ROAD_ID, rr.ROAD_ADDR, rr.ROAD_END, rr.ROAD_LAT, rr.ROAD_LON, rr.ROAD_REG_DT, rr.ROAD_START, rr.ROAD_TITLE, rr.ROAD_TYPE, rr.ROAD_UPDATE)
    """

    if not road_records:
        print("도로 상황 데이터 없음")
        return

    try:
        # executemany로 처리하기 위해 여러 개의 데이터를 담을 배열
        batch_data = []

        for road in road_records:
            values = (
                road['incidentId'],
                road['addressJibun'],
                road['endDate'],
                float(road['locationDataY']),
                float(road['locationDataX']),
                datetime.now(),
                road['startDate'],
                road['incidentTitle'],
                road['incidenteTypeCd'],
                road['updateDate']
            )
            # 각 데이터 batch_data에 넣기
            batch_data.append(values)

        if batch_data:
            # 데이터 하나 삽입할 때마다 커밋하면 많은 시간 소모
            # executemany(): 다중 행을 처리함
            cursor.executemany(insert_sql, batch_data)
            conn.commit()

        print("ROAD 데이터 삽입 완료!")

    except Exception as e:
        print("DB 입력 중 오류 발생:", e)

    finally:
        cursor.close()
        conn.close()
#----------------------------------------------------------------------------

# CCTV 데이터 저장하는 함수
def cctv_list():
    # Oracle DB 연결 설정
    conn = get_db_connection()
    cursor = conn.cursor()

    # API 호출
    url = 'https://openapi.its.go.kr:9443/cctvInfo?apiKey=2ecfc4faab8e4c1fb109a7c6c6493baa&type=ex&cctvType=1&minX=124.60&maxX=131.87&minY=33.06&maxY=38.61&getType=json'
    cctv_data = urllib.request.urlopen(url).read().decode('utf-8')
    data = json.loads(cctv_data)
    cursor.execute("TRUNCATE TABLE CCTV")

    insert_sql = """
    INSERT INTO CCTV (CCTV_LAT, CCTV_LON, CCTV_NAME, CCTV_REG_DT, CCTV_URL)
    VALUES (:1, :2, :3, :4, :5)
    """

    unique_data = set()
    filtered_data = []
    for cctv in data['response']['data']:
        cctv_lat = float(cctv["coordy"])
        cctv_lon = float(cctv["coordx"])

        if(cctv_lat, cctv_lon) not in unique_data:
            unique_data.add((cctv_lat, cctv_lon))
            filtered_data.append((
                cctv_lat,
                cctv_lon,
                cctv["cctvname"],
                datetime.now(),
                cctv["cctvurl"]
            ))

    cursor.executemany(insert_sql, filtered_data)

    conn.commit()
    cursor.close()
    conn.close()
    print("CCTV 데이터 삽입 완료!")

#----------------------------------------------------------------------------

def weather_info_insert():

    con = oracledb.connect(user="ROMA", password="roma1235", dsn="192.168.100.30:1522/XE")
    cursor = con.cursor()
    print(f'con: {con}')
    print(f'csv파일 읽기!')
    cursor.execute("TRUNCATE TABLE WEATHER")

    sql = """
                    INSERT INTO WEATHER (
                               WEATHER_STATION_ID, WEATHER_CLOUD_AMOUNT, WEATHER_GROUND_TEMPERATURE, WEATHER_HUMIDITY,
                               WEATHER_OBSERVATION_TIME, WEATHER_PRECIPITATION,
                               WEATHER_SNOWFALL, WEATHER_TEMPERATURE, WEATHER_VISIBILITY_DISTANCE, WEATHER_WIND_SPEED,
                               WEATHER_REG_DT
                               )
                               VALUES (
                               :1, :2, :3, :4, :5, :6, :7, :8, :9, :10, sysdate                
                               )
                """
    #columns = ["STN", "CA_TOT", "TS", "HM", "TM", "RN_JUN", "SD_DAY", "TA", "VS", "WS"]

    required_columns = {
        'WEATHER_STATION_ID': 1,
        'WEATHER_CLOUD_AMOUNT': 25,
        'WEATHER_GROUND_TEMPERATURE': 36,
        'WEATHER_HUMIDITY': 13,
        'WEATHER_OBSERVATION_TIME': 0,
        'WEATHER_PRECIPITATION': 16,
        'WEATHER_SNOWFALL': 21,
        'WEATHER_TEMPERATURE': 11,
        'WEATHER_VISIBILITY_DISTANCE': 32,
        'WEATHER_WIND_SPEED': 3
    }
    df = pd.read_csv('weather_info.csv')
    df_selected = df.iloc[:, list(required_columns.values())]

    for index, row in df_selected.iterrows():
        cursor.execute(sql, tuple(row))

    con.commit()
    print('날씨 정보 DB 저장 완료!')

def weather_info():
    #https://apihub.kma.go.kr/api/typ01/url/kma_sfctm2.php?stn=0&help=1&authKey=RdYZpapNT9eWGaWqTS_XXg
    domain = "https://apihub.kma.go.kr/api/typ01/url/kma_sfctm2.php?"
    stn_id = "stn=0&"
    #help = "help=1&"
    API_KEY = 'authKey=RdYZpapNT9eWGaWqTS_XXg'

    #url = domain + stn_id + help + API_KEY
    url = domain + stn_id + API_KEY

    #print(url)
    with urlopen(url) as I:
        html_bytes = I.read()
        html = html_bytes.decode('euc-kr')
        #print(html)

    #지상관측 데이터 확인
    response = requests.get(url) #GET 요청
    print(response.text)

    text_data = response.text.replace("#", "")

    #데이터 공백으로 파싱하여 저장
    data = pd.read_csv(io.StringIO(text_data), sep='\\s+',skiprows=2)
    data['REG_DT']= datetime.today().strftime("%Y%m%d")
    print(data)

    # CSV 파일로 저장 (헤더 없이)
    data.to_csv('./weather_info.csv', index=False,header=False)

    with open('./weather_info.csv','r') as f:
        lines = f.readlines()

    with open('./weather_info.csv', 'w') as f:
        f.writelines(lines[:-1]) # 맨 위 첫 줄, 맨 마지막 줄 제외하고 저장


#----------------------------------------------------------------------------

# 저장된 csv파일을 불러 읽어 db에 저장
def news_insert():
    con = oracledb.connect(user="ROMA", password="roma1235", dsn="192.168.100.30:1522/XE")
    cursor = con.cursor()
    print(f'con: {con}')

    print('csv파일 읽기!')

    # with open('news.csv', 'rb') as f:
    #    raw_data = f.read()
    #    result = chardet.detect(raw_data)
    #    print(result)
    # result: {'encoding': 'CP949', 'language': 'Korean', 'confidence': 1.0} 파일 인코딩 형식 확인하는 법
    cursor.execute("TRUNCATE TABLE news")

    insert_sql = """
        INSERT INTO news (news_title, news_reg_dt, news_newsroom, news_uploaded_time, news_url)
        VALUES (:1,sysdate,:3,:4,:5)
        """

    with open('news.csv', 'r', encoding='UTF-8') as f:
        reader = csv.reader(f)
        next(reader)
        for row in reader:
            cursor.execute(insert_sql, row)
    con.commit()
    cursor.close()
    con.close()

    print('뉴스 정보 DB 저장 완료!')


def convert_to_csv(list_title, list_newsroom, list_reg_time, list_news_url):
    fields = ['title', 'newsroom', 'reg_time', 'url']

    # zip()을 사용하면 순회 가능한 객체들을 여러 개 받아도, 인덱스끼리 잘라서 저장시켜줌
    rows = zip(list_title, list_newsroom, list_reg_time, list_news_url)

    with open('news.csv', 'w', newline='', encoding='utf-8') as f:
        write = csv.writer(f)

        write.writerow(fields)
        write.writerows(rows)
    print('csv파일 저장 완료: news.csv')


def news():
    list_title = []  # 뉴스 제목
    list_newsroom = []  # 보도국 이름
    list_reg_time = []  # 기사 등록 시간
    list_news_url = []  # 기사 링크

    url = f'https://news.naver.com/breakingnews/section/103/240'
    # print(f'url: {url}')

    response = requests.get(url)
    # print(f'response: {response.status_code}')

    if 200 == response.status_code:
        html = response.text
        # print(f'html: {html}')

        bs = BeautifulSoup(html, 'html.parser')

        # 뉴스 제목 추출
        titles = bs.select('div.sa_text a .sa_text_strong')
        # print(f'titles: {titles}')

        for title in titles:
            title = title.next
            #print(f'title: {title}')
            list_title.append(title)

        # 보도국 추출
        newsrooms = bs.select('div.sa_text_press')
        # print(f'newsrooms: {newsrooms}')

        for newsroom in newsrooms:
            newsroom = newsroom.next
            #print(f'newsroom: {newsroom}')
            list_newsroom.append(newsroom)

        # 기사 등록 시간 추출
        reg_times = bs.select('div.sa_text_datetime b')
        # print(f'reg_times: {reg_times}')

        for reg_time in reg_times:
            reg_time = reg_time.next
            #print(f'reg_time: {reg_time}')
            list_reg_time.append(reg_time)

        # 기사 링크 추출
        news_urls = bs.select('div.sa_text a')
        # print(f'news_url: {news_url}')

        for a in news_urls:
            news_url = a.get('href')
            if 'comment' in news_url:
                continue
            #print(f'news_url: {news_url}')
            list_news_url.append(news_url)

    return list_title, list_newsroom, list_reg_time, list_news_url

#----------------------------------------------------------------------------

# 공통함수
def get_db_connection():
    dsn = cx.makedsn('192.168.100.30', '1522', service_name='xe')
    try:
        conn = cx.connect(user='ROMA', password='roma1235', dsn=dsn, encoding='utf-8', nencoding='utf-8')
        return conn
    except Exception as e:
        print("DB 연결 실패:", e)
        return

#----------------------------------------------------------------------------

# 기능별 스케줄러

# 사고, 공사 데이터 15분마다
schedule.every(15).minutes.do(road_list)
# 기상단기예보 데이터 매일 07시마다
schedule.every().day.at("07:00").do(road_weather_list)
# CCTV 데이터 6시간마다
schedule.every(6).hours.do(cctv_list)
# 기상청 데이터 받기 매시간 5분
schedule.every().hour.at(":05").do(weather_info)
# 기상청 데이터 DB 저장 매시간 6분
schedule.every().hour.at(":06").do(weather_info_insert)
# 네이버 교통 기사 크롤링 매시간 5분
schedule.every().hour.at(":05").do(news)
# 크롤링한 데이터 csv 만들기 매시간 6분
schedule.every().hour.at(":06").do(lambda: convert_to_csv(*news()))
# 기사 데이터 DB 저장 매사간 7분
schedule.every().hour.at(":07").do(news_insert)

while True:
    schedule.run_pending()
    time.sleep(1)