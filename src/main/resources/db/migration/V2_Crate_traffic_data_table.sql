-- TrafficData 테이블 생성
CREATE TABLE traffic_data(
    id BIGSERIAL PRIMARY KEY,
    location_id BIGINT NOT NULL REFERENCES locations(id) on DELETE CASCADE,
    recorded_at TIMESTAMP NOT NULL,
    vehicle_count INTEGER NOT NULL,
    average_speed DOUBLE PRECISION,
    congestion_level VARCHAR(10) CHECK(congestion_level IN ('LOW', 'MEDIUM', 'HIGH', 'SEVERE')),
    weather_condition VARCHAR(50),
    temperature DOUBLE PRECISION,
    day_of_week INTEGER CHECK (day_of_week BETWEEN 1 AND 7),
    hour_of_day INTEGER CHECK (hour_of_day BETWEEN 0 AND 23),
    is_holiday BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)

-- 인덱스 생성
CREATE INDEX idx_traffic_data_location_datetime ON traffic_data(location_id, recorded_at);
CREATE INDEX idx_traffic_data_recorded_at ON traffic_data(recorded_at);
CREATE INDEX idx_traffic_data_day_hour ON traffic_data(day_of_week, hour_of_day);
CREATE INDEx idx_traffic_data_congestion ON traffic_data(congestion_level);

-- 파티셔닝을 위한 준비(선택 사항)
-- CREATE TABLE traffic_data_2024 PARTITION OF traffic_data
-- FOR VALUES FROM ('2024-01-01') TO ('2025-01-01');