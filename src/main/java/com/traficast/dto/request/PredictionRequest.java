package com.traficast.dto.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionRequest {

    @NotNull(message = "위치 ID는 필수입니다.")
    @Positive(message = "위치 ID는 양수여야 합니다.")
    private Long locationId;

    @NotNull(message = "예측 날짜는 필수입니다.")
    @Future(message = "예측 날짜는 미래여야 합니다.")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime predictionDate;

    @Pattern(regexp = "^(MANUAL|SCHEDULED|API_REQUEST)$",
            message = "예측 타입은 MANUAL, SCHEDULED, API_REQUEST 중 하나여야 합니다")
    private String predictionType = "API_REQUEST";

    @DecimalMin(value = "0.0", message = "신뢰도는 0 이상이어야 합니다.")
    @DecimalMax(value = "1.0", message = "신뢰도는 1 이하여야 합니다.")
    private Double confidenceThreshold = 0.7;

    private String modelVersion;

    @Builder.Default
    private Boolean includeWeatherData = true;

    @Builder.Default
    private Boolean includeHistoricalPattern = true;
}
