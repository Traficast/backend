package com.traficast.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

// 교통량 측정 및 예측 대상이 되는 지리적 위치 정보
@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location extends BaseEntity{

    @Column(name = "location_name", nullable = false, length = 100)
    private String locationName; // 예: "강남역 사거리"

    @Column(name = "latitude", nullable = false, precision = 10, scale = 7)
    private Double latitude; // 위도

    @Column(name = "longitude", nullable = false, precision = 10, scale = 7)
    private Double longitude; // 경도

    @Column(name = "road_type", length = 50)
    private String roadType; // 도로 유형(고속도로, 일반도로, 시내도로)

    @Column(name = "lane_count")
    private Integer laneCount; // 차선 수

    @Column(name = "address", length = 200)
    private String address; // 상세 주소

    @Column(name = "description", length = 500)
    private String description; // 위치 설명

    @Column(name = "speed_limit")
    private Integer speedLimit; //  제한 속도

    // 양방향 관계 설정
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TrafficData> trafficDataList;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PredictionHistory> predictionHistories;
}
