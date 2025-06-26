-- Location 테이블 생성
CREATE TABLE locations(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    address VARCHAR(500),
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 인덱스 생성
CREATE INDEX idx_location_coordinates ON locations(latitude, longitude);
CREATE INDEX idx_location_name ON locations(name);
CREATE INDEX idx_location_active ON locations(active);

-- 샘플 데이터 삽입
INSERT INTO locations(name, latitude, longitude, address, description) VALUES
('강남역 교차로', 37.4979, 127.0276, '서울특별시 강남구 강남대로 지하 396', '강남역 주요 교차로'),
('홍대입구역', 37.5563, 126.9236, '서울특별시 마포구 양화로 지하 188', '홍대입구역 주변 도로'),
('명동역', 37.5636, 126.9810, '서울특별시 중구 명동길 지하 14', '명동 중심가'),
('잠실대교', 37.5194, 127.0720, '서울특별시 송파구 올림픽로 240', '잠실대교 진입로'),
('여의도 한강공원', 37.5285, 126.9327, '서울특별시 영등포구 여의공원로 지하 68', '여의도 한강공원 입구');