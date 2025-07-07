package com.traficast.dto.request;


import com.traficast.entity.TrafficData;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

// 대량의 교통 데이터 업로드
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "교통 데이터 업로드 ")
public class DataUploadRequest {

    @NotEmpty(message = "업로드할 교통 데이터 목록은 필수입니다.")
    @Size(min = 1, max = 1000, message = "한번에 1-1000개의 데이터를 업로드할 수 있습니다")
    @Valid
    @Schema(description = "교통 데이터 목록", required = true)
    private List<TrafficDataEntry> trafficDataEntries;

    @Size(max = 100, message = "데이터 출처는 100자 이하여야 합니다")
    @Schema(description = "데이터 출처", example = "서울시 교통정보센터")
    private String dataSource;

    @Size(max = 500, message = "업로드 설명은 500자 이하여야 합니다")
    @Schema(description = "업로드 설명", example = "2024년 1월 강남구 교통 데이터")
    private String description;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "개별 교통 데이터 항목")
    public static class TrafficDataEntry{

        @NotNull(message = "위치ID는 필수입니다")
        @Positive(message = "위치 ID는 양수여야 합니다")
        @Schema(description = "위치 ID", example = "1", required = true)
        private Long locationId;

        @NotNull(message = "측정 시간은 필수입니다")
        @PastOrPresent(message = "측정 시간은 현재 시간 이전이어야 합니다")
        @Schema(description = "측정 시간", example = "2024-01-15T14:30:00", required = true)
        private LocalDateTime measuredAt;

        @NotNull(message = "차량 수는 필수입니다")
        @Min(value = 0, message = "차량 수는 0 이상이어야 합니다")
        @Max(value = 10000, message = "차량 수는 10000 이하여야 합니다")
        @Schema(description = "차량 수", example = "120", required = true)
        private Integer vehicleCount;

        @DecimalMin(value = "0.0", message = "평균 속도는 0 이상이어야 합니다")
        @DecimalMax(value = "200.0", message = "평균 속도는 200 이하여야 합니다")
        @Schema(description = "평균 속도 (km/h)", example = "45.5")
        private Double averageSpeed;

        @NotNull(message = "혼잡도는 필수입니다")
        @Schema(description = "혼잡도", example = "NORMAL", required = true)
        private TrafficData.CongestionLevel congestionLevel;

        // 환경 요인
        @Pattern(regexp = "^(맑음|흐림|비|눈|안개|기타)$",
                message = "날씨 상태는 맑음, 흐림, 비, 눈, 안개, 기타 중 하나여야 합니다")
        @Schema(description = "날씨 상태", example = "맑음")
        private String weatherCondition;

        @DecimalMin(value = "-50.0", message = "온도는 -50도 이상이어야 합니다")
        @DecimalMax(value = "60.0", message = "온도는 60도 이하여야 합니다")
        @Schema(description = "온도 (°C)", example = "22.5")
        private Double temperature;

        @DecimalMin(value = "0.0", message = "습도는 0% 이상이어야 합니다")
        @DecimalMax(value = "100.0", message = "습도는 100% 이하여야 합니다")
        @Schema(description = "습도 (%)", example = "65.0")
        private Double humidity;

        @DecimalMin(value = "0.0", message = "가시거리는 0km 이상이어야 합니다")
        @DecimalMax(value = "50.0", message = "가시거리는 50km 이하여야 합니다")
        @Schema(description = "가시거리 (km)", example = "15.0")
        private Double visibility;

        @Schema(description = "공휴일 여부", example = "false")
        private Boolean isHoliday;

        @Min(value = 1, message = "요일은 1(월요일) 이상이어야 합니다")
        @Max(value = 7, message = "요일은 7(일요일) 이하여야 합니다")
        @Schema(description = "요일 (1=월요일, 7=일요일)", example = "1")
        private Integer dayOfWeek;

        @Min(value = 0, message = "시간은 0 이상이어야 합니다")
        @Max(value = 23, message = "시간은 23 이하여야 합니다")
        @Schema(description = "시간 (0-23)", example = "14")
        private Integer hourOfDay;
    }
}
