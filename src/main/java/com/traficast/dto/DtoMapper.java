package com.traficast.dto;

import com.traficast.dto.response.LocationResponse;
import com.traficast.dto.response.PredictionResponse;
import com.traficast.entity.Location;
import com.traficast.entity.PredictionHistory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DtoMapper {

    public PredictionResponse toPredictionResponse(PredictionHistory prediction){
        if(prediction == null){
            return null;
        }

        return PredictionResponse.builder()
                .predictionId(prediction.getId())
                .locationSummary(PredictionResponse.LocationSummary.builder()
                        .locationId(prediction.getLocation().getId())
                        .locationName(prediction.getLocation().getLocationName())
                        .latitude(prediction.getLocation().getLatitude())
                        .longitude(prediction.getLocation().getLongitude())
                        .build())
                .predictionDate(prediction.getPredictionTime())
                .targetDate(prediction.getTargetDateTime())
                .predictedVehicleCount(prediction.getPredictedVehicleCount())
                .predictedSpeed(prediction.getPredictedSpeed())
                .predictedCongestionLevel(prediction.getPredictedCongestionLevel())
                .confidenceScore(prediction.getConfidenceScore())
                .predictionType(prediction.getPredictionType())
                .build();
    }

    // ======= Location 변환 메서드들 ========
    /**
     * Location 엔티티를 LocationResponse Dto로 변환
     */
    public LocationResponse toLocationResponse(Location location){
        if(location == null){
            return null;
        }

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

    /**
     * Location 엔티티 리스트를 LocationResponse DTO 리스트로 변환
     */
    public List<LocationResponse> toLocationResponseList(List<Location> locations){
        if(locations == null){
            return List.of(); // null 안전성 확보
        }
        return locations.stream()
                .map(this::toLocationResponse)
                .collect(Collectors.toList());
    }

    /**
     * Location 엔티티를 LocationSummary로 변환(PredictionResponse 내부 사용)
     */
    public PredictionResponse.LocationSummary toLocationSummary(Location location){
        if(location == null){
            return null;
        }

        return PredictionResponse.LocationSummary.builder()
                .locationId(location.getId())
                .locationName(location.getLocationName())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
    }
}
