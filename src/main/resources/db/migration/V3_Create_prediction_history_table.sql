-- PredictionHistory 테이블 생성
CREATE TABLE prediction_history (
    id BIGSERIAL PRIMARY KEY,
    location_id BIGINT NOT NULL REFERENCES locations(id) ON DELETE CASCADE,
    prediction_date TIMESTAMP NOT NULL,
    predicted_vehicle_count INTEGER,
    predicted_congestion_level VARCHAR(10) CHECK (predicted_congestion_level IN ('LOW', 'MEDIUM', 'HIGH', 'SEVERE')),
    confidence_score DOUBLE PRECISION CHECK (confidence_score BETWEEN 0 AND 1),
    model_version VARCHAR(50),
    prediction_type VARCHAR(20) NOT NULL CHECK (prediction_type IN ('MANUAL', 'SCHEDULED', 'API_REQUEST')),
    actual_vehicle_count INTEGER,
    accuracy_score DOUBLE PRECISION CHECK (accuracy_score BETWEEN 0 AND 1),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 인덱스 생성
CREATE INDEX idx_prediction_location_date ON prediction_history(location_id, prediction_date);
CREATE INDEX idx_prediction_created_at ON prediction_history(created_at);
CREATE INDEX idx_prediction_type ON prediction_history(prediction_type);
CREATE UNIQUE INDEX idx_prediction_unique ON prediction_history(location_id, prediction_date);

-- 트리거 함수 생성 (updated_at 자동 업데이트)
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END
$$ language 'plpgsql';

-- 트리거 적용
CREATE TRIGGER update_locations_updated_at BEFORE UPDATE ON locations
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_traffic_data_updated_at BEFORE UPDATE ON traffic_data
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_prediction_history_updated_at BEFORE UPDATE ON prediction_history
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();