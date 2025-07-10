package com.traficast.service;


import com.traficast.dto.response.PredictionResponse;
import com.traficast.exception.PredictionException;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpHeaders;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModelApiService {

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
    public PredictionResponse requestPredictionFromModel(Map<String, Object> request){
        LocalDateTime startTime = LocalDateTime.now();
    }

    /* 모델 API 상태 확인 */
    public boolean isModelApiHealthy(){
        String healthUrl = modelApiUrl + "/health";

        try{
            ResponseEntity<String> response = restTemplate.getForEntity(healthUrl, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e){
            log.warn("모델 API 상태 확인 실패: { }", e.getMessage());
            return false;
        }
    }
}
