package com.traficast.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// 예측 모델의 실행 결과와 검증 데이터를 저장합니다
@Entity
@Table(name = "prediction_history",
        indexes = {
            @Index(name = "idx_location_prediction_time",
                columnList = "location_id, prediction_time"),
                @Index(name = "idx_prediction_target_time", columnList = "target_datetime")
        })

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionHistory extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "prediction_time", nullable = false)
    private LocalDateTime predictionTime; // 예측 수행 시간

    @Column(name = "target_datetime", nullable = false)
    private LocalDateTime targetDateTime; // 예측 대상 시간

    // 예측 결과
    @Column(name = "predicted_vehicle_count", nullable = false)
    private Integer predictedVehicleCount; // 예측된 차량 수

    @Column(name = "predicted_speed", nullable = false, precision = 5)
    private Double predictedSpeed; // 예측된 평균 속도

    @Column(name = "predicted_congestion_level")
    @Enumerated(EnumType.STRING)
    private TrafficData.CongestionLevel predictedCongestionLevel; // 예측된 혼잡도

    @Column(name = "confidence_score", precision = 5)
    private Double confidenceScore; // 예측 신뢰도(0.0 ~ 1.0)

    @Column(name = "prediction_type")
    @Enumerated(EnumType.STRING)
    private PredictionType predictionType; // 예측 타입

    // 검증용 실제 값(나중에 업데이트)
    @Column(name = "actual_vehicle_count")
    private Integer actualVehicleCount; // 실제 값 (검증용)

    @Column(name = "actual_speed", precision = 5)
    private Double actualSpeed;

    @Column(name = "actual_congestion_level")
    @Enumerated(EnumType.STRING)
    private TrafficData.CongestionLevel actualCongestionLevel;

    @Column(name = "accuracy_score", precision = 5)
    private Double accuracyScore; // 예측 정확도(실제값과 비교)

    public enum PredictionType{
        HOURLY("시간별 예측"),
        DAILY("일별 예측"),
        WEEKLY("주별 예측"),
        REAL_TIME("실시간 예측");

        private final String description;

        PredictionType(String description){
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
