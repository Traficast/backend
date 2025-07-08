package com.traficast.dto.response;


import com.traficast.entity.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 위치 정보 조회 결과를 위한 응답 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "위치 정보 응답")
public class LocationResponse {

    @Schema(description = "위치 ID", example = "1")
    private Long id;

    @Schema(description = "위치명", example = "강남역 사거리")
    private String locationName;

    @Schema(description = "상세 주소", example = "서울특별시 강남구 강남대로 396")
    private String address;

    @Schema(description = "위도", example = "37.4979")
    private Double latitude;

    @Schema(description = "경도", example = "127.0276")
    private Double longitude;

    @Schema(description = "도로 유형", example = "시내도로")
    private String roadType;

    @Schema(description = "차선 수", example = "4")
    private Integer laneCount;

    @Schema(description = "제한 속도 (km/h)", example = "60")
    private Integer speedLimit;

    @Schema(description = "위치 설명", example = "강남역 2번 출구 앞 주요 교차로")
    private String description;

    @Schema(description = "등록 시간", example = "2024-01-15T14:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시간", example = "2024-01-15T14:30:00")
    private LocalDateTime updatedAt;

    // 추가 통계 정보
    @Schema(description = "최근 교통 데이터 수", example = "1440")
    private Integer recentDataCount;

    @Schema(description = "최근 측정 시간", example = "2024-01-15T14:25:00")
    private LocalDateTime lastMeasuredAt;

    @Schema(description = "평균 차량 수 (최근 24시간)", example = "85")
    private Double averageVehicleCount;

    @Schema(description = "평균 속도 (최근 24시간)", example = "42.5")
    private Double averageSpeed;

    // 엔티티를 DTO로 변환하는 정적 팩토리 메서드
    public static LocationResponse fromEntity(Location location) {
        return LocationResponse.builder()
                .id(location.getId())
                .locationName(location.getLocationName())
                .address(location.getAddress())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .roadType(location.getRoadType())
                .laneCount(location.getLaneCount())
                .speedLimit(location.getSpeedLimit())
                .description(location.getDescription())
                .createdAt(location.getCreatedAt())
                .updatedAt(location.getUpdatedAt())
                .build();
    }
}
