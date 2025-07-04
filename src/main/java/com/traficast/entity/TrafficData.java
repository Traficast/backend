package com.traficast.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// 과거 교통량 데이터와 환경 요인을 포함한 종합적인 교통 정보를 저장
@Entity
@Table(name = "traffic_data",
       indexes = {
        @Index(name="idx_traffic_data_location_datetime",
            columnList = "location_id, recorded_at"),
        @Index(name="idx_traffic_data_recorded_at",
            columnList = "recorded_at")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrafficData extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt; // 기록 시간

    @Column(name = "vehicle_count", nullable = false)
    private Integer vehicleCount; // 차량 수

    @Column(name = "average_speed", precision = 5, scale = 2)
    private Double averageSpeed; // 평균 속도(km/h)

    @Column(name = "congestion_level")
    @Enumerated(EnumType.STRING)
    private CongestionLevel congestionLevel;

    // 환경 요인(예측 정확도 향상을 위한 추가 데이터)
    @Column(name = "weather_condition", length = 50)
    private String weatherCondition; // 날씨 상태

    @Column(name = "temperature", precision = 4, scale = 1)
    private Double temperature; // 온도

    @Column(name = "humidity", precision = 5, scale = 2)
    private Double humidity; // 습도

    @Column(name = "visibility", precision = 5, scale = 2)
    private Double visibility; // 가시거리(km)

    // 시간적 특성
    @Column(name = "is_holiday")
    private Boolean isHoliday; // 공휴일 여부

    @Column(name = "day_of_week")
    private Integer dayOfWeek; // 요일(1=월요일, 7=일요일)

    @Column(name = "hour_of_day")
    private Integer hourOfDay; // 시간(0~23)

    // 혼잡도 열거형
    public enum CongestionLevel{
        SMOOTH("원활"),
        NORMAL("보통"),
        SLOW("서행"),
        CONGESTED("정체");

        private final String description;

        CongestionLevel(String description){
            this.description = description;
        }

        public String getDescription(){
            return description;
        }
    }
}
