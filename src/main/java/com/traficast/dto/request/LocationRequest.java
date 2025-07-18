package com.traficast.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 위치 등록 및 수정을 위한 요청
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "위치 정보 등록/수정 요청")
public class LocationRequest {

    @NotBlank(message = "위치명은 필수입니다")
    @Size(min = 2, max = 100, message = "위치명은 2-100자 사이여야 합니다")
    @Schema(description = "위치명", example = "강남역 사거리", required = true)
    private String locationName;

    @Size(max = 200, message = "주소는 200자 이하여야 합니다")
    @Schema(description = "상세 주소", example = "서울특별시 강남구 강남대로 396")
    private String addrss;

    @NotNull(message = "위도가 필수입니다")
    @DecimalMin(value = "-90.0", message = "위도가 -90도 이상이어야 합니다")
    @DecimalMax(value = "90.0", message = "위도가 90도 이상이어야 합니다")
    @Schema(description = "위도", example = "37.4979", required = true)
    private Double latitude;

    @NotNull(message = "경도가 필수입니다")
    @DecimalMin(value = "-180.0", message = "위도가 -180도 이상이어야 합니다")
    @DecimalMax(value = "180.0", message = "위도가 180도 이상이어야 합니다")
    @Schema(description = "경도", example = "127.0726", required = true)
    private Double longitude;

    @Pattern(regexp = "^(고속도로|일반도로|시내도로|국도|지방도)$",
            message = "도로 유형은 고속도로, 일반도로, 시내도로, 국도, 지방도 중 하나여야 합니다")
    @Schema(description = "도로 유형", example = "시내도로")
    private String roadType;

    @Min(value = 1, message = "차선 수는 1개 이상이어야 합니다")
    @Max(value = 20, message = "차선 수는 20개 이하여야 합니다.")
    @Schema(description = "차선 수", example = "4")
    private Integer laneCount;

    @Min(value = 10, message = "제한 속도는 10km/h 이상이어야 합니다")
    @Max(value = 200, message = "제한 속도는 200km/h 이하여야 합니다")
    @Schema(description = "제한 속도(km/h)", example = "60")
    private Integer speedLimit;

    @Size(max = 500, message = "설명은 500자 이하여야 합니다")
    @Schema(description = "위치 설명", example = "강남역 2번 출구 앞 주요 교차로")
    private String description;

}
