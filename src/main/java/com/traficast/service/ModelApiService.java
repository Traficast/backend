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
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModelApiService {

    private final RestTemplate restTemplate;

    @Value("${model-api.url}")
    private String modelApiUrl;

    @Value("${model-api.timout:30000}")
    private int timeout;

    /* 외부 예측 모델 API 호출 */
    public PredictionResponse requestPrediction(Map<String, Object> request){
        String url = modelApiUrl + "/predict";

        log.info("모델 API 호출 시작: {}", url);

        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<PredictionResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    PredictionResponse.class
            );

            if(response.getStatusCode().is2xxSuccessful() && response.getBody() != null){
                log.info("모델 API 호출 성공");
                return response.getBody();
            }else {
                throw new PredictionException("모델 API에서 올바른 응답을 받지 못했습니다");
            }
        } catch (Exception e) {
            log.error("모델 API 호출 실패: {}", e.getMessage(), e);
            throw new PredictionException("모델 API 호출 중 오류가 발생했습니다: " + e.getMessage());
        }
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
