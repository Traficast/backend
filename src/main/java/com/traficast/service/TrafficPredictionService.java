package com.traficast.service;


import com.traficast.dto.request.PredictionRequest;
import com.traficast.dto.response.PredictionResponse;
import com.traficast.entity.Location;
import com.traficast.entity.PredictionHistory;
import com.traficast.entity.TrafficData;
import com.traficast.repository.LocationRepository;
import com.traficast.repository.PredictionHistoryRepository;
import com.traficast.repository.TrafficDataRepository;
import com.traficast.exception.PredictionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TrafficPredictionService {

    private final LocationRepository locationRepository;
    private final TrafficDataRepository trafficDataRepository;
    private final PredictionHistoryRepository predictionHistoryRepository;
    private final ModelApiService modelApiService;

    /* 교통량 예측 수행 */
    public PredictionResponse predictTraffic(PredictionRequest request){
        log.info("교통량 예측 시작 - 위치:{}, 날짜: {}", request.getLocationId(), request.getPredictionDate());

        // 1. 위치 정보 조회
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new PredictionException(
                        "위치를 찾을 수 없습니다: " + request.getLocationId()));

        if(!location.getActive())
            throw new PredictionException("비활성화된 위치입니다: " + location.getName());

        // 2. 기존 예측 결과 확인
        Optional<PredictionHistory> existingPrediction = predictionHistoryRepository.findByLocationAndPredictionDate(
                location, request.getPredictionDate());

        if(existingPrediction.isPresent()){
            log.info("기존 예측 결과 반환 - ID: {}", existingPrediction.get().getPredictionDate());
            return convertToResponse(existingPrediction.get());
        }

        // 3. 과거 데이터 조회
        List<TrafficData> historicalData = getHistoricalData(location, request.getPredictionDate());

        if(historicalData.isEmpty()){
            throw new PredictionException("예측에 필요한 과거 데이터가 부족합니다");
        }

        // 4. 외부 모델 API 호출
        PredictionResponse modelResult = callModelApi(location, request, historicalData);

        // 5. 예측 결과 저장
        PredictionHistory savedPrediction = savePredictionResult(location, request, modelResult);

        log.info("교통량 예측 완료 - 예측 ID: {}, 예상 차량 수: {}", savedPrediction.getId(), savedPrediction.getPredictedVehicleCount());
        return convertToResponse(savedPrediction);
    }

    /* 위치별 예측 이력 조회 */
    @Transactional(readOnly = true)
    public List<PredictionResponse> getPredictionHistory(Long locationId, LocalDateTime startDate, LocalDateTime endDate){
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new PredictionException("위치를 찾을 수 없습니다: " + locationId));

        List<PredictionHistory> histories =
                predictionHistoryRepository.findByLocationAndPredictionDateBetweenOrderByPredictionDateDesc(
                        location, startDate, endDate);

        return histories.stream()
                .map(this::convertToResponse)
                .toList();
    }

    private List<TrafficData> getHistoricalData(Location location, LocalDateTime predictionDate){
        LocalDateTime endDate = predictionDate.minusDays(1);
        LocalDateTime startDate = endDate.minusDays(30); // 최근 30일 데이터

        return trafficDataRepository.findByLocationAndRecorededAtBetweenOrderByRecordedAt(
                location, startDate, endDate);
    }

    private PredictionResponse callModelApi(Location location, PredictionRequest request, List<TrafficData> historicalData){
        try{
            // 모델 API 호출을 위한 데이터 준비
            Map<String, Object> modelRequest = prepareModelRequest(location, request, historicalData);

            // 외부 모델 API 호출
            return modelApiService.requestPrediction(modelRequest);
        } catch (Exception e) {
            log.error("모델 APi 호출 실패", e);
            throw new PredictionException("예측 모델 서비스에 문제가 발생했습니다: " + e.getMessage());
        }
    }

    private Map<String, Object> prepareModelRequest(Location location, PredictionRequest request, List<TrafficData> historicalData){
        Map<String, Object> modelRequest = new HashMap<>();
        modelRequest.put("locationId", location.getId());
        modelRequest.put("locationName", location.getName());
        modelRequest.put("latitude", location.getLatitude());
        modelRequest.put("longitude", location.getLongtitude());
        modelRequest.put("predictionDate", request.getPredictionDate());
        modelRequest.put("historicalData", historicalData);
        modelRequest.put("includeWeather", request.getIncludeWeatherData());
        modelRequest.put("includePattern", request.getIncludeHistoricalPattern());

        return modelRequest;
    }

    private PredictionHistory savePredictionResult(Location location, PredictionRequest request, PredictionResponse modelResult){
        PredictionHistory prediction = PredictionHistory.builder()
                .location(location)
                .predictionDate(request.getPredictionDate())
                .predictedVehicleCount(modelResult.getPredictedVehicleCount())
                .predictedCongestionLevel(modelResult.getPredictedCongestionLevel())
                .confidenceScore(modelResult.getConfidenceScore())
                .modelVersion(modelResult.getModelVersion())
                .predictionType(PredictionHistory.PredictionType.valueOf(request.getPredictionType()))
                .build();

        return predictionHistoryRepository.save(prediction);
    }

    private PredictionResponse convertToResponse(PredictionHistory prediction){
        return PredictionResponse.builder()
                .predictionId(prediction.getId())
                .locationId(prediction.getLocation().getId())
                .locationName(prediction.getLocation().getName())
                .predictionDate(prediction.getPredictionDate())
                .predictedVehicleCount(prediction.getPredictedVehicleCount())
                .predictedCongestionLevel(prediction.getPredictedCongestionLevel())
                .confidenceScore(prediction.getConfidenceScore())
                .modelVersion(prediction.getModelVersion())
                .createdAt(prediction.getCreatedAt())
                .location(PredictionResponse.LocationInfo.builder()
                        .id(prediction.getLocation().getId())
                        .name(prediction.getLocation().getName())
                        .latitude(prediction.getLocation().getLatitude())
                        .longitude(prediction.getLocation().getLongtitude())
                        .address(prediction.getLocation().getAddress())
                        .build())
                .build();
    }
}
