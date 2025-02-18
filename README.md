# ROAD_FC
간단한 Git 사용법

# 로컬에서 Branch 생성 후 원격 저장소로 Branch 추가
### 1. 프로젝트 폴더 내에서 마우스 우클릭 -> Open Git Bash

### 2. 현재 브랜치에서 새로운 브랜치 생성
`$ git checkout -b 새로운_브랜치명`

### 3. 새로운 브랜치를 원격(GitHub)으로 푸시
`$ git push origin 새로운_브랜치명`


# 로컬에서 변경사항 원격으로 푸시하는 방법
### 1. 수정한 프로젝트 모두 저장

### 2. 수정된 파일 확인
`$ git status`

### 3. 수정된 파일을 스테이징
#### 모든 파일 추가하기
`git add .`
#### 특정 파일 추가하기
`git add 파일명`

### 4. 변경 사항 커밋
`git commit -m "커밋 메시지"`

### 5. 원격 저장소(GitHub)로 푸시 (Push)
`git push origin 현재_브랜치명`
