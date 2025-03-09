import time
import cv2
import argparse
import torch
import cx_Oracle
from pathlib import Path

DB_USERNAME = "ROMA"
DB_PASSWORD = "roma1235"
DB_DSN = "218.144.130.138:1522/XE"

# 모델 불러오기
# github 모델, 모델명, pretrained: 학습된 이미지셋 사용
model = torch.hub.load('ultralytics/yolov5', 'yolov5s', pretrained=True)

# 실행할 때마다 새로운 폴더 만들기
def increment_path(path, exist_ok=False):
    path = Path(path)
    if path.exists() and not exist_ok:
        dirs = [d for d in path.parent.iterdir() if d.stem.startswith(path.stem)]
        n = max([int(d.stem[len(path.stem):]) for d in dirs if d.stem[len(path.stem):].isdigit()], default=0) + 1
        path = path.with_name(f"{path.stem}{n}")
    path.mkdir(parents=True, exist_ok=True)
    return path

# DB 연결 함수
def connect_db():
    try:
        connection = cx_Oracle.connect(DB_USERNAME, DB_PASSWORD, DB_DSN)
        cursor = connection.cursor()
        print("DB 연결 성공!")
        return connection, cursor
    except cx_Oracle.DatabaseError as e:
        print(f"DB 연결 실패: {e}")
        return None, None

# 탐지 데이터를 DB에 저장하는 함수
def save_to_db(object_name, confidence, image_path, object_count):
    connection, cursor = connect_db()
    if connection and cursor:
        sql = """INSERT INTO YOLO_DETECTIONS (YOLO_CONFIDENCE, YOLO_IMAGE_PATH, YOLO_OBJECT_COUNT, YOLO_OBJECT_NAME)
                 VALUES (:1, :2, :3, :4)"""
        try:
            cursor.execute(sql, (confidence, image_path, object_count, object_name))
            connection.commit()
            print(f"DB 저장 완료: {object_name} ({object_count}개), 신뢰도: {confidence}, 이미지: {image_path}")
        except cx_Oracle.DatabaseError as e:
            print(f"DB 저장 실패: {e}")
        finally:
            cursor.close()
            connection.close()

# 인자값 추가
# 터미널에서 URL 또는 파일 입력받기
parser = argparse.ArgumentParser()
parser.add_argument("--source", type=str, required=True, help="CCTV URL")
args = parser.parse_args()

# 저장할 디렉토리 (YOLOv5처럼 자동 증가)
save_dir = increment_path(Path("runs/detect/cctv"))
print(f"저장 경로: {save_dir}")

# --source 뒤 CCTV URL 정보 불러오기
source = args.source
# --source 뒤 CCTV URL 동영상 캡쳐
cap = cv2.VideoCapture(source)

# FPS 가져오기
# OpenCV를 사용해서 cap 객채를 가져옴
fps = cap.get(cv2.CAP_PROP_FPS)
# 0이나 None 값이면 옳지 않은 값
if fps == 0 or fps is None:
    # 기본값 30
    fps = 30

# 5초마다 저장할 프레임 개수

frame_interval = int(fps * 5)
frame_count = 0

# 시간 제한 주기
start_time = time.time()
max_duration = 1 * 60

# DB에 넣을 객체 지정
target_classes = ["car", "truck", "bus"]

while cap.isOpened():
    # 영상을 한 프레임씩 읽기
    # 두 개의 값을 반환
    ret, frame = cap.read()
    # 새로운 프레임을 못받았을 때 break
    if not ret:
        break

    frame_count += 1

    # 5초마다 한 번씩 YOLOv5로 탐지 후 저장
    if frame_count % frame_interval == 0:
        # YOLOv5 탐지 실행
        results = model(frame)

        # 탐지된 이미지 가져오기
        detected_img = results.render()[0]

        # 저장 파일명 생성
        timestamp = time.strftime("%Y%m%d-%H%M%S")
        save_path = str(save_dir / f"{timestamp}.jpg")

        # YOLOv5가 탐지한 결과 이미지 저장
        cv2.imwrite(save_path, detected_img)
        print(f"YOLO 탐지 이미지 저장됨: {save_path}")

        # DB에 탐지된 객체 정보 저장 (객체 개수 포함)
        detections = results.pandas().xyxy[0]
        # target_classes: data -> coco.yaml 파일에 학습되어 있는 객체 중 DB에 넣을 객체 선별
        # target_classes와 일치한 이름을 가진 detections
        filtered_detections = detections[detections['name'].isin(target_classes)]

        # 객체별 개수 세기
        object_counts = filtered_detections['name'].value_counts().to_dict()

        for obj_name, count in object_counts.items():
            conf = detections[detections['name'] == obj_name]['confidence'].mean()
            save_to_db(obj_name, conf, save_path, count)

    # 화면 출력
    # winname: 윈도우 이름, mat: 받는 인자
    cv2.imshow("YOLOv5", frame)

    # waitKey(키 입력 대기 시간)
    # ESC = 27
    # 1초 후 ESC 키를 누르면 종료
    # cv2.waitKey()가 32비트로 반환할 수 있기 때문에 0xFF를 사용해서 8비트 값 유지시키기
    if cv2.waitKey(1) & 0xFF == 27:
        break

    if time.time() - start_time >= max_duration:
        print("1분 분석 후 자동 종료합니다")
        break


# 사용한 자원 해제
cap.release()
# 창 닫기
cv2.destroyAllWindows()
