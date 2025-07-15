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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TrafficPredictionService {

    private final LocationRepository locationRepository;
    private final TrafficDataRepository trafficDataRepository;
    private final PredictionHistoryRepository predictionHistoryRepository;
    private final ModelApiService modelApiService;

    /**
     * 교통량 예측을 수행하고 결과를 저장합니다.
     * @param request 예측 요청 DTO
     * @return 예측 결과 응답 DTO 목록
     */
    @Transactional
    public List<PredictionResponse> predictTraffic(PredictionRequest request){
        log.info("교통량 예측 요청 처리 시작 - 시간={}, 위치 수={}",
                request.getTargetDatetime(), request.getLocationIds().size());

        LocalDateTime startTime = LocalDateTime.now();

        // 1. 요청 유효성 검증
        validatePredictionRequest(request);

        // 2. 위치 정보 조회 및 검증
        List<Location> targetLocations = getAndValidateLocations(request.getLocationIds());

        // 3. 외부 ML 모델 API 호출
        Map<String, Object> rawPredictionResult = modelApiService.requestPredictionFromModel(request);

        // 4. 예측 결과 파싱 및 저장
        List<PredictionResponse> predictionResponses = processPredictionResults(
                rawPredictionResult, targetLocations, request);
        )
    }

    /**
     * 예측 요청의 유효성을 검증합니다.
     */
    private void validatePredictionRequest(PredictionRequest request){
        if(request.getLocationIds() == null || request.getLocationIds().isEmpty()){
            throw new IllegalArgumentException("위치 ID 목록은 필수입니다.");
        }

        if(request.getTargetDatetime() == null){
            throw new IllegalArgumentException("예측 대상 시간은 필수입니다.");
        }

        if(request.getTargetDatetime().isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("예측 대상 시간은 현재 시간 이후여야 합니다.");
        }

        if(request.getLocationIds().size() > 100){
            throw new IllegalArgumentException("한 번에 최대 100개 위치까지 예측 가능합니다.");
        }
    }

    /**
     * 위치 정보를 조회하고 유효성을 검증합니다.
     */
    private List<Location> getAndValidateLocations(List<Long> locationIds){
        List<Location> locations = locationRepository.findAllById(locationIds);

        if(locations.size() != locationIds.size()){
            List<Long> foundIds = locations.stream()
                    .map(Location::getId)
                    .collect(Collectors.toList());
            List<Long> missingIds = locationIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());
            throw new NoSuchElementException("다음 위치 ID를 찾을 수 없습니다: " + missingIds);
        }
        return locations;
    }

    /**
     * 모델 API 응답을 파싱하고 예측 결과를 저장합니다.
     */
    private List<PredictionResponse> processPredictionResults(
            Map<String,Object> rawResult, List<Location> locations, PredictionRequest request
    ){
        List<PredictionResponse> responses = new ArrayList<>();

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> predictions = (List<Map<String, Object>>) rawResult.get("predictions");

        if(predictions == null || predictions.isEmpty()){
            log.warn("모델 API로부터 예측 결과가 반환되지 않았습니다.");
            return responses;
        }

        String modelVersion = (String) rawResult.getOrDefault("modelVersion", "unknown");

        for(Map<String, Object> predictionData: predictions){
            try{
                PredictionResponse response = processSinglePrediction(
                        predictionData, locations, request, modelVersion
                );
                responses.add(response);
            } catch (Exception e) {
                log.error("개별 예측 결과 처리 중 오류 발생: {}", e.getMessage(), e);
            }
        }

        return responses;
    }

    /**
     * 개별 예측 결과를 처리합니다.
     */
    private PredictionResponse processSinglePrediction(
            Map<String, Object> predictionData, List<Location> locations,
            PredictionRequest request, String modelVersion
    ){
        Long locationId = ((Number)predictionData.get("locationId")).longValue();
        Integer predictedVehicleCount = (Integer) predictionData.get("predictedVehicleCount");
        Double predictedSpeed = ((Number)predictionData.get("predictedSpeed")).doubleValue();
        String congestionLevelStr = (String) predictionData.get("predictedCongestionLevel");
        Double confidenceScore = ((Number) predictionData.get("confidenceScore")).doubleValue();

        Location location = locations.stream()
                .filter(loc -> loc.getId().equals(locationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("예측된 위치 ID가 유효하지 않습니다." + locationId));

        // PredictionHistory 엔티티 생성 및 저장
        PredictionHistory predictionHistory = PredictionHistory.builder()
                .location(location)
                .predictionTime(LocalDateTime.now())
                .targetDateTime(request.getTargetDatetime())
                .predictedVehicleCount(predictedVehicleCount)
                .predictedSpeed(predictedSpeed)
                .predictedCongestionLevel(TrafficData.CongestionLevel.valueOf(congestionLevelStr.toUpperCase()))
                .confidenceScore(confidenceScore)
                .predictionType(request.getPredictionType())
                .build();

        PredictionHistory savedPrediction = predictionHistoryRepository.save(predictionHistory);

        log.debug("예측 결과 저장 완료: ID={}, Location={}, Confidence={}",
                savedPrediction.getId(), location.getLocationName(), confidenceScore);

        return new PredictionResponse(savedPrediction);
    }

    /**
     *  특정 위치의 최신 예측 결과를 조회합니다.
     */
    public Optional<PredictionResponse> getLatestPredictionForLocation(Long locationId){
        Location location = locationRepository.findById(locationId)
                .orElseThrow(()-> new NoSuchElementException("위치 ID" + locationId + "를 찾을 수 없습니다."));

        return predictionHistoryRepository.findTopByLocationOrderByPredictionTimeDesc(location)
                .map(PredictionResponse);
    }

    /**
     * 예측 정확도 검증을 수행합니다.
     */
    @Transactional
    public void validatePredictionAccuracy(){
        log.info("예측 정확도 검증 작업 시작");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDay = now.minusDays(1);

        // 1일 전 예측 결과 중 아직 검증되지 않은 것들 조회
        List<PredictionHistory> unvalidatedPredictions = predictionHistoryRepository
                .findByPredctionTimeBetweenAndActualVehicleCountIsNull(oneDay, now);

        int validatedCount = 0;

        for(PredictionHistory prediction: unvalidatedPredictions){
            try{
                // 실제 교통 데이터 조회
                Optional<TrafficData> actualData = trafficDataRepository
                        .findByLocationAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                                prediction.getLocation(),
                                prediction.getTargetDateTime().minusMinutes(30),
                                prediction.getTargetDateTime().plusMinutes(30))
                        .stream().findFirst();

                if(actualData.isPresent()){
                    // 정확도 계산 및 업데이트
                    TrafficData actual = actualData.get();
                    double accuracy = caculateAccuracy(prediction, actual);

                    prediction.setActualVehicleCount(actual.getVehicleCount());
                    prediction.setActualSpeed(actual.getAverageSpeed());
                    prediction.setActualCongestionLevel(actual.getCongestionLevel());
                    prediction.setAccuracyScore(accuracy);

                    predictionHistoryRepository.save(prediction);
                    validatedCount++;
                }
            } catch (Exception e) {
                log.error("예측 정확도 검증 중 오류 발생: Prediction ID={}, Error={}",
                        prediction.getId(), e.getMessage());
            }
        }

        log.info("예측 정확도 검증 완료: 총 {}개 예측 결과 검증", validatedCount);
    }

    /**
     * 예측 정확도를 계산합니다
     */
    private double caculateAccuracy(PredictionHistory prediction, TrafficData actual){
        double vehicleCountAccuracy = 1.0 - Math.abs(prediction.getPredictedVehicleCount() -
                actual.getVehicleCount()) / (double) Math.max(prediction.getPredictedVehicleCount(),
                actual.getVehicleCount());

        double speedAccuracy = 1.0;
        if(prediction.getPredictedSpeed() != null && actual.getAverageSpeed() != null){
            speedAccuracy = 1.0 - Math.abs(prediction.getPredictedSpeed() - actual.getAverageSpeed())
                    / Math.max(prediction.getPredictedSpeed(), actual.getAverageSpeed());
        }

        double congestionAccuracy = prediction.getPredictedCongestionLevel().equals(actual.getCongestionLevel()) ? 1.0: 0.0;

        // 가중평균으로 전체 정확도 계산
        return (vehicleCountAccuracy * 0.4 + speedAccuracy * 0.3 + congestionAccuracy * 0.3);
    }



}
