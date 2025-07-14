package com.traficast.service;


import com.traficast.dto.request.PredictionRequest;
import com.traficast.dto.response.PredictionResponse;
import com.traficast.entity.ModelConfig;
import com.traficast.exception.PredictionException;
import com.traficast.repository.ModelConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModelApiService {

    private final ModelConfigRepository modelConfigRepository;
    private final RestTemplate restTemplate;

    @Value("${model.api.default-url:http://localhost:8000/predict}")
    private String defaultModelApiUrl;

    @Value("${model-api.timout:30}")
    private int apiTimeoutSeconds;

    /**
     * 외부 머신러닝 모델 API에 예측 요청을 보냅니다.
     * @param request 예측 요청 DTO
     * @return 모델 API로부터 받은 예측 결과
     * @throws RuntimeException 모델 API 통신 중 오류 발생 시
     */
    public PredictionResponse requestPredictionFromModel(PredictionRequest request){
        LocalDateTime startTime = LocalDateTime.now();

        // 1. 활성화된 모델 설정 조회
        ModelConfig activeModel = getActiveModel();
        String modelApiEndpoint = activeModel.getApiEndpoint();

        if(modelApiEndpoint == null || modelApiEndpoint.isEmpty()){
            log.warn("활성화된 모델의 API 엔드포인트가 없습니다. 기본 URL 사용:{}", defaultModelApiUrl);
            modelApiEndpoint = defaultModelApiUrl;
        }

        log.info("모델 API 호출 시작: Endpoint={}, Model Version={}", modelApiEndpoint, activeModel.getModelVersion());

        // 2. HTTP 요청 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Model-Version", activeModel.getModelVersion());
        headers.set("X-Request-ID", generateRequestId());

        // 3. 요청 데이터 준비
        Map<String, Object> requestBody = prepareRequestBody(request);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 4. 모델 API 호출 및 응답 처리
        try{
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    modelApiEndpoint, entity, Map.class
            );

            Duration executionTime = Duration.between(startTime, LocalDateTime.now());
            log.info("모델 API 호출 성공: 실행시간={}ms, 상태코드={}",
                    executionTime.toMillis(), response.getStatusCode());

            Map<String, Object> responseBody = response.getBody();
            if(responseBody == null){
                throw new RuntimeException("모델 API 응답이 비어있습니다.");
            }

            // 5. 응답 검증
            validateModelResponse(responseBody);
            return responseBody;
        }catch (HttpClientErrorException e){
            log.error("모델 API 클라이언트 오류: Status={}, Body={}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("모델 API 클라이언트 오류: " + e.getResponseBodyAsString(), e);
        }catch (HttpServerErrorException e){
            log.error("모델 API 서버 오류: Status={}, Body={}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("모델 API 서버 오류: " + e.getResponseBodyAsString(), e);
        }catch (Exception e){
            Duration executionTime = Duration.between(startTime, LocalDateTime.now());
            log.error("모델 API 호출 중 예외 발생: 실행시간 ={}ms, Error={}",
                    executionTime.toMillis(), e.getMessage(), e);
            throw new RuntimeException("모델 API 호출 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 활성화된 모델 설정을 조회합니다.
     */
    private ModelConfig getActiveModel(){
        return modelConfigRepository.findTopByIsActiveTrueAndIsDeletedFalseOrderByActivationDateDesc()
                .orElseGet(() -> {
                    log.warn("활성화된 모델을 찾을 수 없습니다. 기본 모델 설정을 생성합니다.");
                    return ModelConfig.builder()
                            .modelName("Default Traffic Predictor")
                            .modelVersion("1.0.0")
                            .apiEndpoint(defaultModelApiUrl)
                            .isActive(true)
                            .build();
                });
    }

    /**
     * 예측 요청 데이터를 모델 API 형식으로 변환합니다.
     */
    private Map<String, Object> prepareRequestBody(PredictionRequest request){
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("locationIds", request.getLocationIds());
        requestBody.put("targetDatetime", request.getTargetDatetime().toString());
        requestBody.put("predictionType", request.getPredictionType().name());
        requestBody.put("minimumConfidence", request.getMinimumConfidence());

        // 환경 요인 추가
        if(request.getExpectedWeather() != null){
            requestBody.put("expectedWeather", request.getExpectedWeather());
        }
        if(request.getExpectedTemperature() != null){
            requestBody.put("expectedTemperature", request.getExpectedTemperature());
        }
        if(request.getIsHoliday() != null){
            requestBody.put("isHoliday", request.getIsHoliday());
        }

        return requestBody;
    }

    /**
     * 모델 API 응답의 유효성을 검증합니다
     */
    private void validateModelResponse(Map<String, Object> response){
        if(!response.containsKey("predictions") || !(response.get("predictions") instanceof List)){
            throw new RuntimeException("모델 API 응답 형식이 올바르지 않습니다: predictions 필드가 없거나 배열이 아닙니다.");
        }
    }

    /**
     * 요청 ID를 생성합니다
     */
    private String generateRequestId(){
        return "req-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }


}
