package com.traficast.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @Column(name = "vehicle_count", nullable = false)
    private Integer vehicleCount;

    @Column(name = "average_speed")
    private Double averageSpeed;

    @Column(name = "congestion_level")
    @Enumerated(EnumType.STRING)
    private CongestionLevel congestionLevel;

    @Column(name = "weather_condition")
    private String weatherCondition;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "day_of_week")
    private Integer dayOfWeek; // 1=Monday, 7=Sunday

    @Column(name = "hour_of_day")
    private Integer hourOfDay; // 0~23

    @Column(name = "is_holiday")
    private Boolean isHoliday = false;

    public enum CongestionLevel{
        LOW, MEDIUM, HIGH, SEVERE
    }
}
