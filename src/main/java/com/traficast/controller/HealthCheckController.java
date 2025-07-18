package com.traficast.controller;


import com.traficast.dto.response.ApiResponse;
import com.traficast.service.ModelApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "시스템 상태 체크 API", description = "시스템 헬스체크 및 상태 모니터링 API")
public class HealthCheckController {

    /**
     * 기본 헬스체크 (로드밸런서용)
     */
    @GetMapping
    @Operation(summary = "기본 헬스체크", description = "시스템 기본 상태를 확인합니다.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        Map<String, Object> status = new HashMap<>();

        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now());
        status.put("service", "traficast-backend");
        status.put("version", "1.0.0");

        return ResponseEntity.ok(ApiResponse.success("시스템이 정상 작동 중입니다.", status));
    }

    /**
     * 간단한 헬스체크 (로드밸런서/쿠버네티스용)
     */
    @GetMapping("/simple")
    @Operation(summary = "간단한 헬스체크", description = "로드밸런서용 간단한 상태 확인")
    public ResponseEntity<String> simpleHealthCheck(){
        return ResponseEntity.ok("UP");
    }
}
