package com.traficast.dto.response;

import com.traficast.entity.PredictionHistory;
import com.traficast.entity.TrafficData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

// 예측 결과를 위한 포괄적인 응답 DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "교통량 예측 결과 응답")
public class PredictionResponse {

    @Schema(description = "예측 ID", example = "1")
    private Long predictionId;

    @Schema(description = "위치 정보")
    private LocationSummary locationSummary;

    // 내부 클래스들
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "위치 요약 정보")
    public static class LocationSummary{

        @Schema(description = "위치 ID", example = "1")
        private Long locationId;

        @Schema(description = "위치명", example = "강남역 사거리")
        private String locationName;

        @Schema(description = "위도", example = "37,4979")
        private Double latitude;

        @Schema(description = "경도", example = "127.0276")
        private Double longitude;
    }

    @Schema(description = "예측 수행 시간", example = "2024-01-15T14:30:00")
    private LocalDateTime predictionDate;

    @Schema(description = "예측 대상 시간", example = "2024-01-15T16:00:00")
    private LocalDateTime targetDate;

    @Schema(description = "예측된 차량 수", example = "120")
    private Integer predictedVehicleCount;

    @Schema(description = "예측된 평균 속도(km/h)", example = "45.5")
    private double predictedSpeed;

    @Schema(description = "예측된 혼잡도", example = "NORMAL")
    private TrafficData.CongestionLevel predictedCongestionLevel;

    @Schema(description = "예측 신뢰도 (0.0 ~ 1.0)", example = "0.85")
    private Double confidenceScore;

    @Schema(description = "사용된 모델 버전", example = "2.1.0")
    private String modelVersion;

    @Schema(description = "예측 타입", example = "HOURLY")
    private PredictionHistory.PredictionType predictionType;

    @Schema(description = "예측 구간별 세부 정보")
    private List<PredictionDetail> predictionDetails;

    @Schema(description = "예측 근거 정보")
    private PredictionRationale rationale;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "예측 구간별 세부 정보")
    public static class PredictionDetail{
        @Schema(description = "시간", example = "2024-01-15T16:00:00")
        private LocalDateTime datetime;

        @Schema(description = "예측 차량 수", example = "120")
        private Integer vehicleCount;

        @Schema(description = "예측 속도", example = "45.5")
        private Double speed;

        @Schema(description = "혼잡도", example = "NORMAL")
        private TrafficData.CongestionLevel congestionLevel;

        @Schema(description = "신뢰도", example = "0.85")
        private Double confidence;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "예측 근거 정보")
    public static class PredictionRationale{
        @Schema(description = "주요 영향 요인")
        private List<String> keyFactors;

        @Schema(description = "과거 유사 패턴", example = "지난 주 같은 시간대 대비 15% 증가 예상")
        private String historicalPattern;

        @Schema(description = "날씨 영향", example = "맑은 날씨로 인한 교통량 증가 예상")
        private String weatherImpact;

        @Schema(description = "특별 이벤트 영향")
        private String specialEventImpact;
    }

    // 엔티티를 DTO로 변환하는 정적 팩토리 메서드
    public static PredictionResponse fromEntity(PredictionHistory predictionHistory) {
        return PredictionResponse.builder()
                .predictionId(predictionHistory.getId())
                .locationSummary(LocationSummary.builder()
                        .locationId(predictionHistory.getLocation().getId())
                        .locationName(predictionHistory.getLocation().getLocationName())
                        .latitude(predictionHistory.getLocation().getLatitude())
                        .longitude(predictionHistory.getLocation().getLongitude())
                        .build())
                .predictionDate(predictionHistory.getPredictionTime())
                .targetDate(predictionHistory.getTargetDateTime())
                .predictedVehicleCount(predictionHistory.getPredictedVehicleCount())
                .predictedSpeed(predictionHistory.getPredictedSpeed())
                .predictedCongestionLevel(predictionHistory.getPredictedCongestionLevel())
                .confidenceScore(predictionHistory.getConfidenceScore())
                .predictionType(predictionHistory.getPredictionType())
                .build();
    }
}
