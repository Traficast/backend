
# 🚦 교통량 예측 시스템 백엔드 API

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![Gradle](https://img.shields.io/badge/Gradle-8.0-lightgrey.svg)](https://gradle.org/)
[![Swagger](https://img.shields.io/badge/Swagger-OpenAPI%203.0-85EA2D.svg)](https://swagger.io/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

**LSTM(Long Short-Term Memory) 기반 실시간 교통량 예측 시스템의 백엔드 API**

머신러닝을 활용하여 도시 교통 패턴을 분석하고, 정확한 교통량 예측을 통해 교통 관리 효율성을 높이는 엔터프라이즈급 REST API 시스템입니다.

---

## 📋 목차

- [🎯 프로젝트 개요](#-프로젝트-개요)
- [✨ 주요 기능](#-주요-기능)
- [🛠️ 기술 스택](#️-기술-스택)
- [🏗️ 프로젝트 구조](#️-프로젝트-구조)
- [📦 설치 및 실행](#-설치-및-실행)
- [🔧 환경 설정](#-환경-설정)
- [📚 API 문서](#-api-문서)
- [💾 데이터베이스 스키마](#-데이터베이스-스키마)
- [🔍 사용 예제](#-사용-예제)
- [🤝 기여 방법](#-기여-방법)
- [📄 라이선스](#-라이선스)

---

## 🎯 프로젝트 개요

### **배경**
도시화 진행에 따른 교통 체증 문제 해결을 위해 정확한 교통량 예측이 필수적입니다. 본 시스템은 데이터 기반 교통 관리 의사결정을 지원하는 백엔드 API를 제공합니다.

### **목표**
- **실시간 교통량 예측**: LSTM 모델 기반 정확한 예측 서비스
- **효율적인 데이터 관리**: 대용량 교통 데이터 처리 및 저장
- **확장 가능한 아키텍처**: 마이크로서비스 전환 대비 설계
- **운영 환경 최적화**: 엔터프라이즈급 보안 및 모니터링 체계

---

## ✨ 주요 기능

### 🔮 **교통량 예측**
- **시간별/일별/주별 예측**: 다양한 시간 단위 교통량 예측
- **실시간 예측**: 현재 상황 기반 즉시 예측 서비스
- **외부 ML 모델 연동**: Python 기반 T-GCN 모델과 REST API 통신
- **예측 정확도 검증**: 실제 데이터와 비교한 자동 정확도 평가

### 📍 **위치 관리**
- **위치 CRUD**: 교통 측정 지점 등록, 조회, 수정, 삭제
- **지리적 검색**: GPS 좌표 기반 반경 내 위치 검색
- **위치 정보 관리**: 도로 유형, 차선 수, 제한 속도 등 상세 정보

### 📊 **데이터 관리**
- **대용량 업로드**: 교통 데이터 일괄 업로드 및 검증
- **데이터 품질 관리**: 자동 데이터 검증 및 오류 처리
- **통계 분석**: 교통 패턴 분석 및 통계 제공

### 🕒 **스케줄링 시스템**
- **자동 예측**: 일일/시간별 주기적 교통량 예측
- **배치 처리**: 대용량 데이터 백그라운드 처리
- **수동 트리거**: 관리자용 배치 작업 수동 실행 API

### 🔧 **시스템 관리**
- **헬스 체크**: 시스템 상태 모니터링 및 가용성 확인
- **통합 예외 처리**: 일관된 오류 응답 체계
- **API 문서화**: Swagger 기반 자동 문서 생성
- **로깅 시스템**: 포괄적인 로그 관리 및 모니터링

---

## 🛠️ 기술 스택

### **백엔드 프레임워크**
- **Spring Boot 3.2.0**: 핵심 애플리케이션 프레임워크
- **Spring Security**: 인증 및 권한 관리 (JWT 확장 준비)
- **Spring Data JPA**: 데이터 접근 계층 및 ORM
- **Spring Web**: RESTful API 구현

### **데이터베이스**
- **PostgreSQL 15**: 주 데이터베이스
- **HikariCP**: 고성능 커넥션 풀
- **JPA/Hibernate**: 객체 관계 매핑

### **외부 연동**
- **RestTemplate**: ML 모델 API 통신
- **Apache HttpComponents**: HTTP 클라이언트 최적화
- **Jackson**: JSON 데이터 처리

### **문서화 및 모니터링**
- **Swagger/OpenAPI 3.0**: API 문서 자동 생성
- **Spring Boot Actuator**: 애플리케이션 모니터링
- **Logback**: 로깅 시스템

### **개발 도구**
- **Java 17**: 프로그래밍 언어
- **Gradle**: 빌드 도구
- **Lombok**: 보일러플레이트 코드 제거

---

## 🏗️ 프로젝트 구조

```
src/main/java/com/traficast/
├── 📂 config/                    # 시스템 설정 클래스
│   ├── DatabaseConfig.java       # 데이터베이스 및 JPA 설정
│   ├── RestTemplateConfig.java   # HTTP 클라이언트 설정
│   ├── SchedulerConfig.java      # 스케줄링 설정
│   ├── SecurityConfig.java       # 보안 설정
│   └── SwaggerConfig.java        # API 문서화 설정
├── 📂 controller/                # REST API 엔드포인트
│   ├── DataUploadController.java
│   ├── HealthCheckController.java
│   ├── LocationController.java
│   ├── SchedulerController.java
│   └── TrafficPredictionController.java
├── 📂 dto/                       # 데이터 전송 객체
│   ├── mapper/DtoMapper.java     # Entity-DTO 변환
│   ├── request/                  # 요청 DTO
│   └── response/                 # 응답 DTO
├── 📂 entity/                    # JPA 엔티티
│   ├── BaseEntity.java           # 공통 기본 엔티티
│   ├── Location.java             # 위치 정보
│   ├── TrafficData.java          # 교통 데이터
│   ├── PredictionHistory.java    # 예측 결과
│   ├── ModelConfig.java          # 모델 설정
│   ├── User.java                 # 사용자 정보
│   └── SystemLog.java            # 시스템 로그
├── 📂 exception/                 # 예외 처리
│   ├── GlobalExceptionHandler.java
│   ├── DataUploadException.java
│   └── PredictionException.java
├── 📂 repository/                # 데이터 접근 계층
│   ├── LocationRepository.java
│   ├── TrafficDataRepository.java
│   ├── PredictionHistoryRepository.java
│   ├── ModelConfigRepository.java
│   ├── UserRepository.java
│   └── SystemLogRepository.java
└── 📂 service/                   # 비즈니스 로직
    ├── TrafficPredictionService.java
    ├── LocationService.java
    ├── DataUploadService.java
    ├── ModelApiService.java
    └── SchedulerService.java
```

---

## 📦 설치 및 실행

### **사전 요구사항**
- **Java 17 이상**
- **PostgreSQL 15 이상**
- **Gradle 8.0 이상** (Wrapper 포함)
- **외부 ML 모델 API** (선택사항, 예측 기능 사용 시 필요)

### **1. 프로젝트 클론**
```bash
git clone https://github.com/your-org/traffic-prediction-system.git
cd traffic-prediction-system
```

### **2. 데이터베이스 설정**
```sql
-- PostgreSQL에서 실행
CREATE DATABASE traffic_prediction;
CREATE USER traffic_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE traffic_prediction TO traffic_user;
```

### **3. 환경 변수 설정**
```bash
# 환경 변수 또는 .env 파일 설정
export DB_USERNAME=traffic_user
export DB_PASSWORD=your_password
export MODEL_API_URL=http://localhost:8000/predict
```

### **4. 애플리케이션 빌드 및 실행**
```bash
# 빌드
./gradlew build

# 실행
./gradlew bootRun

# 또는 JAR 파일 직접 실행
java -jar build/libs/traffic-prediction-system-1.0.0.jar
```

### **5. 실행 확인**
- **메인 애플리케이션**: http://localhost:8080
- **API 문서 (Swagger)**: http://localhost:8080/swagger-ui.html
- **헬스 체크**: http://localhost:8080/api/v1/health

---

## 🔧 환경 설정

### **application.yml 주요 설정**

```yaml
# 서버 설정
server:
  port: 8080

# 데이터베이스 설정
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/traffic_prediction
    username: ${DB_USERNAME:traffic_user}
    password: ${DB_PASSWORD:traffic_password}
    driver-class-name: org.postgresql.Driver
  
  # JPA 설정
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

# 외부 ML 모델 API 설정
model:
  api:
    default-url: ${MODEL_API_URL:http://localhost:8000/predict}
    timeout: 30

# 스케줄러 설정
scheduler:
  enabled: true
  daily-prediction-cron: "0 0 0 * * *"    # 매일 자정
  hourly-prediction-cron: "0 0 * * * *"   # 매시간 정각
  accuracy-validation-cron: "0 0 3 * * *" # 매일 새벽 3시

# 로깅 설정
logging:
  level:
    com.traficast: INFO
    org.springframework.web: WARN
  file:
    name: logs/traffic-prediction.log
```

---

## 📚 API 문서

### **주요 엔드포인트**

| Method | Endpoint | Description | 인증 필요 |
|--------|----------|-------------|-----------|
| **POST** | `/api/v1/predictions` | 교통량 예측 요청 | No |
| **GET** | `/api/v1/predictions/locations/{id}/latest` | 최신 예측 결과 조회 | No |
| **POST** | `/api/v1/locations` | 새 위치 등록 | No |
| **GET** | `/api/v1/locations` | 위치 목록 조회 | No |
| **GET** | `/api/v1/locations/nearby` | 주변 위치 검색 | No |
| **POST** | `/api/v1/data/upload` | 교통 데이터 업로드 | No |
| **GET** | `/api/v1/health` | 시스템 상태 확인 | No |
| **POST** | `/api/v1/scheduler/daily-prediction` | 일일 예측 수동 실행 | No |

### **API 응답 형식**

**성공 응답**
```json
{
  "success": true,
  "message": "요청이 성공적으로 처리되었습니다.",
  "data": {
    "predictionId": 123,
    "predictedVehicleCount": 150,
    "predictedSpeed": 45.5,
    "confidenceScore": 0.85
  },
  "timestamp": "2024-01-15T14:30:00"
}
```

**오류 응답**
```json
{
  "success": false,
  "message": "위치 ID 1을 찾을 수 없습니다.",
  "errorCode": "RESOURCE_NOT_FOUND",
  "timestamp": "2024-01-15T14:30:00"
}
```

---

## 💾 데이터베이스 스키마

### **주요 테이블**

- **`locations`**: 교통량 측정 지점의 지리적 정보
- **`traffic_data`**: 실제 측정된 교통량 데이터
- **`prediction_history`**: ML 모델의 예측 결과 및 검증 데이터
- **`model_configs`**: 배포된 ML 모델의 설정 및 성능 정보
- **`users`**: 시스템 사용자 정보 (인증/권한 관리용)
- **`system_logs`**: 시스템 로그 및 에러 기록

### **주요 관계**
- `Location` ↔ `TrafficData` (1:N)
- `Location` ↔ `PredictionHistory` (1:N)
- 모든 엔티티는 `BaseEntity`를 상속하여 생성/수정 시간 자동 관리

---

## 🔍 사용 예제

### **1. 기본 워크플로우**

```bash
# 1. 새 위치 등록
curl -X POST "http://localhost:8080/api/v1/locations" \
  -H "Content-Type: application/json" \
  -d '{
    "locationName": "강남역 사거리",
    "address": "서울특별시 강남구 강남대로 396",
    "latitude": 37.4979,
    "longitude": 127.0276,
    "roadType": "시내도로",
    "laneCount": 4,
    "speedLimit": 60
  }'

# 2. 교통 데이터 업로드
curl -X POST "http://localhost:8080/api/v1/data/upload" \
  -H "Content-Type: application/json" \
  -d '{
    "dataSource": "서울시 교통정보센터",
    "trafficDataEntries": [{
      "locationId": 1,
      "measuredAt": "2024-01-15T14:30:00",
      "vehicleCount": 120,
      "averageSpeed": 45.5,
      "congestionLevel": "NORMAL"
    }]
  }'

# 3. 교통량 예측 요청
curl -X POST "http://localhost:8080/api/v1/predictions" \
  -H "Content-Type: application/json" \
  -d '{
    "locationIds": [1],
    "targetDatetime": "2024-01-15T16:00:00",
    "predictionType": "HOURLY",
    "minimumConfidence": 0.8
  }'

# 4. 예측 결과 조회
curl -X GET "http://localhost:8080/api/v1/predictions/locations/1/latest"
```

### **2. 주변 위치 검색 및 일괄 예측**

```bash
# 주변 위치 검색
curl -X GET "http://localhost:8080/api/v1/locations/nearby?latitude=37.4979&longitude=127.0276&radius=5.0"

# 스케줄러 수동 실행
curl -X POST "http://localhost:8080/api/v1/scheduler/daily-prediction"
```

---



---

## 📄 라이선스

이 프로젝트는 **Apache License 2.0** 하에 배포됩니다.

```
Copyright 2024 Traffic Prediction System Contributors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

## 🙏 감사의 말

이 프로젝트는 다음 오픈소스 프로젝트들의 도움을 받았습니다:
- **Spring Framework** - 견고한 애플리케이션 프레임워크
- **PostgreSQL** - 강력한 오픈소스 데이터베이스
- **Swagger/OpenAPI** - API 문서화 표준

---



