package com.traficast.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prediction_history",
        indexes = {
            @Index(name = "idx_prediction_location_date",
                columnList = "location_id, prediction_date"),
                @Index(name = "idx_prediction_created_at", columnList = "created_at")
        })

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionHistory extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "prediction_date", nullable = false)
    private LocalDateTime predictionDate;

    @Column(name = "predicted_vehicle_count")
    private Integer predictedVehicleCount;

    @Column(name = "predicted_congestion_level")
    @Enumerated(EnumType.STRING)
    private TrafficData.CongestionLevel predictedCongestionLevel;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "model_version")
    private String modelVersion;

    @Column(name = "prediction_type")
    @Enumerated(EnumType.STRING)
    private PredictionType predictionType;

    @Column(name = "actual_vehicle_count")
    private Integer actualVehicleCount; // 실제 값 (검증용)

    @Column(name = "accuracy_score")
    private Double accuracyScore; // 예측 정확도

    public enum PredictionType{
        MANUAL, SCHEDULED, API_REQUEST
    }
}
