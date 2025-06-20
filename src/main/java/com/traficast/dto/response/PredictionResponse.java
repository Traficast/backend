package com.traficast.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.traficast.entity.TrafficData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionResponse {

    private Long predictionId;
    private Long locationId;
    private String locationName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime predictionDate;

    private Integer predictedVehicleCount;
    private TrafficData.CongestionLevel predictedCongestionLevel;
    private Double confidenceScore;
    private String modelVersion;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private Map<String, Object> additionalInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LocationInfo{
        private Long id;
        private String name;
        private Double latitude;
        private Double longitude;
        private String address;
    }

    private LocationInfo location;
}
