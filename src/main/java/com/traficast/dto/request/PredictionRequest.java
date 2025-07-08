package com.traficast.dto.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.traficast.entity.PredictionHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

// 포괄적인 요청 DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "교통량 예측 요청")
public class PredictionRequest {

    @NotNull(message = "위치 ID 목은는 필수입니다.")
    @Size(max = 50, message = "한 번에 최대 50개 위치까지 예측 가능합니다")
    @Schema(description = "예측할 위치 ID 목록", example = "[1,2,3]", required = true)
    private List<@Positive(message = "위치 ID는 양수여야 합니다") Long> locationIds;

    @NotNull(message = "예측 날짜는 필수입니다.")
    @Future(message = "예측 날짜는 미래여야 합니다.")
    @Schema(description = "예측 대상 시간", example = "2024-01-15T16:00:00", readOnly = true)
    private LocalDateTime targetDatetime;

    @NotNull(message = "예측 타입은 필수입니다")
    @Schema(description = "예측 타입", example = "HOURLY", required = true)
    private PredictionHistory.PredictionType predictionType;

    @Size(max = 50, message = "모델 버전은 50자 이하여야 합니다")
    @Schema(description = "사용할 모델 버전(선택 사항)", example = "2.1.0")
    private String preferredModelVersion;

    @DecimalMin(value = "0.0", message = "신뢰도는 0.0 이상이어야 합니다.")
    @DecimalMax(value = "1.0", message = "신뢰도는 1.0 이하여야 합니다.")
    @Schema(description = "요구되는 최소 신뢰도", example = "0.8")
    private Double minimumConfidence;

    // 예측에 영향을 줄 수 있는 환경 요인
    @Schema(description = "예상 날씨 상태", example = "맑음")
    private String expectedWeather;

    @Schema(description = "예상 온도 (°C)", example = "25.0")
    private Double expectedTemperature;

    @Schema(description = "공휴일 여부", example = "false")
    private Boolean isHoliday;

    @Schema(description = "특별 이벤트 여부", example = "false")
    private Boolean hasSpecialEvent;

    @Size(max = 200, message = "특별 이벤트 설명은 200자 이하여야 합니다")
    @Schema(description = "특별 이벤트 설명", example = "강남 페스티벌 개최")
    private String specialEventDescription;
}
