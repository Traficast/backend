package com.traficast.controller;


import com.traficast.dto.response.ApiResponse;
import com.traficast.service.ModelApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/health")
@RequiredArgsConstructor
public class HealthCheckController {

    private final ModelApiService modelApiService;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        Map<String, Object> healthStatus = new HashMap<>();

        healthStatus.put("status", "UP");
        healthStatus.put("timestamp", LocalDateTime.now());
        healthStatus.put("service", "traficast-backend");
        healthStatus.put("version", "1.0.0");

        // 외부 서비스 상태 확인
        boolean modelApiHealthy = modelApiService.isModelApiHealthy();
        healthStatus.put("modelApiStatus", modelApiHealthy ? "UP" : "DOWN");

        Map<String, Object> dependencies = new HashMap<>();
        dependencies.put("database", "UP"); // 실제로는 DB 연결 확인 필요
        dependencies.put("modelApi", modelApiHealthy ? "UP" : "DOWN");
        healthStatus.put("dependencies", dependencies);

        return ResponseEntity.ok(ApiResponse.success(healthStatus));
    }
}
