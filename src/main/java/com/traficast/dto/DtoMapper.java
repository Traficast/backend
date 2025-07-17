package com.traficast.dto;

import com.traficast.dto.response.PredictionResponse;
import com.traficast.entity.PredictionHistory;

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
}
