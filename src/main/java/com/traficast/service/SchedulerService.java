package com.traficast.service;

import com.traficast.dto.request.PredictionRequest;
import com.traficast.entity.Location;
import com.traficast.entity.PredictionHistory;
import com.traficast.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final TrafficPredictionService trafficPredictionService;
    private final LocationRepository locationRepository;

    /**
     * 매일 자정에 다음날 교통량 예측을 수행합니다.
     * 크론 표현식: "0 0 0 * * *" = 매일 자정(0시 0분 0초)
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void dailyTrafficPredictionJob(){
        log.info("==== [스케줄러] 일일 교통량 예측 작업 시작: {} ===", LocalDateTime.now());

        try{
            // 1. 활성화된 위치 조회
            List<Location> activeLocations = locationRepository.findByIsDeletedFalseOrderByLocationName();
            if(activeLocations.isEmpty()){
                log.warn("예측할 활성화된 위치가 없습니다. 일일 예측 작업을 건너뜁니다.");
                return;
            }

            List<Long> locationIds = activeLocations.stream()
                    .map(Location::getId)
                    .collect(Collectors.toList());

            // 2. 다음 날 0시부터 24시간 예측 요청 생성
            LocalDateTime tommorrowMidnight = LocalDateTime.now()
                    .plusDays(1)
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0);

            PredictionRequest predictionRequest = PredictionRequest.builder()
                    .locationIds(locationIds)
                    .targetDatetime(tommorrowMidnight)
                    .predictionType(PredictionHistory.PredictionType.DAILY)
                    .minimumConfidence(0.7) // 최소 신뢰도 70%
                    .build();

            // 3. 예측 수행
            List<com.traficast.dto.response.PredictionResponse> results =
                    trafficPredictionService.predictTraffic(predictionRequest);

            log.info("=== [스케줄러] 일일 교통량 예측 작업 완료: {}개 위치, {}개 예측 결과 생성 ===",
                    locationIds.size(), results.size());
        }catch (Exception e){
            log.error("=== [스케줄러] 일일 교통량 예측 작업 중 오류 발생: {} ===", e.getMessage(), e);
        }
    }

    /**
     * 매 시간 정각에 다음 1시간 후의 실시간 교통량 예측을 수행한다
     * 크론 표현식: "0 0 * * * *" = 매시간 정각(0분 0초)
     */
    @Scheduled(cron = "0 0 * * * *")
    public void hourlyTrafficPredictionJob(){
        log.info("=== [스케줄러] 시간별 교통량 예측 작업 시작: {} ===", LocalDateTime.now());

        try{
            // 1. 주요 위치만 선별하여 예측(성능 최적화)
            List<Location> majorLocations = locationRepository.findByIsDeletedFalseOrderByLocationName()
                    .stream()
                    .limit(20) // 상위 20개 위치만 시간별 예측
                    .collect(Collectors.toList());

            List<Long> locationIds = majorLocations.stream()
                    .map(Location::getId)
                    .collect(Collectors.toList());

            // 2. 1시간 후 예측 요청 생성
            LocalDateTime nextHour = LocalDateTime.now()
                    .plusHours(1)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0);

            PredictionRequest predictionRequest = PredictionRequest.builder()
                    .locationIds(locationIds)
                    .targetDatetime(nextHour)
                    .predictionType(PredictionHistory.PredictionType.HOURLY)
                    .minimumConfidence(0.8) // 시간별 예측은 더 높은 신뢰도 요구
                    .build();

            // 3. 예측 수행
            List<com.traficast.dto.response.PredictionResponse> results =
                    trafficPredictionService.predictTraffic(predictionRequest);

            log.info("==== [스케줄러] 시간별 교통량 예측 작업 완료: {}개 위치, {}개 예측 결과 생성 ====",
                    locationIds.size(), results.size());
        } catch (Exception e) {
            log.error("==== [스케줄러] 시간별 교통량 예측 작업 중 오류 발생: {} ====", e.getMessage(), e);
        }
    }

    /**
     * 매일 새벽 3시에 예측 정확도 검증 작업을 수행합니다.
     * 크론 표현식: "0 0 3 * * *" = 매일 새벽 3시
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void predictionAccuracyValidationJob() {
        log.info("==== [스케줄러] 예측 정확도 검증 작업 시작: {} ====", LocalDateTime.now());

        try {
            // 예측 정확도 검증 로직 구현
            // 실제 교통 데이터와 예측 결과를 비교하여 정확도 계산
            trafficPredictionService.validatePredictionAccuracy();

            log.info("==== [스케줄러] 예측 정확도 검증 작업 완료 ====");

        } catch (Exception e) {
            log.error("==== [스케줄러] 예측 정확도 검증 작업 중 오류 발생: {} ====", e.getMessage(), e);
        }
    }
}
