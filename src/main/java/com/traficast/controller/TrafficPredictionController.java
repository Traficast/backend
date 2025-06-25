package com.traficast.controller;


import com.traficast.dto.request.PredictionRequest;
import com.traficast.dto.response.ApiResponse;
import com.traficast.dto.response.PredictionResponse;
import com.traficast.service.TrafficPredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/predictions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "교통 예측", description = "교통량 예측 관련 API")
public class TrafficPredictionController {

    private final TrafficPredictionService predictionService;

    @PostMapping
    @Operation(summary = "교통량 예측", description = "특정 위치와 시간에 대한 교통량을 예측합니다.")
    public ResponseEntity<ApiResponse<PredictionResponse>> predictTraffic(
            @Valid @RequestBody PredictionRequest request){
        log.info("교통량 예측 요청 - 위치: {}, 날짜: {}", request.getLocationId(), request.getPredictionDate());

        PredictionResponse response = predictionService.predictTraffic(request);

        return ResponseEntity.ok(ApiResponse.success("예측 이력을 성공적으로 조회했습니다", response));
    }

    @GetMapping("/history")
    @Operation(summary = "예측 이력 조회", description = "특정 위치의 예측 이력을 조회합니다")
    public ResponseEntity<ApiResponse<PredictionResponse>> getPredictionHistory(
            @Parameter(description = "위치 ID") @RequestParam Long locationId,
            @Parameter(description = "시작 날짜")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @Parameter(description = "종료 날짜")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {

        log.info("예측 이력 조회 요청 - 위치: {}, 기간 {} ~ {}", locationId, startDate, endDate);

        List<PredictionResponse> history = predictionService.getPredictionHistory(locationId, startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success("예측 이력을 성공적으로 조회했습니다.", history));
    }

    @GetMapping("/{predictionId}")
    @Operation(summary = "예측 결과 단건 조회", description = "특정 예측 결과를 조회합니다")
    public ResponseEntity<ApiResponse<PredictionResponse>> getPrediction(
            @Parameter(description = "예측 ID") @PathVariable Long predictionId){
        log.info("예측 결과 조회 요청 - ID: {}", predictionId);

        // 서비스 메서드 추가 필요
        // PredictionResponse response = predictionService.getPredictionById(predictionId);

        return ResponseEntity.ok(ApiResponse.success("예측 결과가 성공적으로 조회했습니다.", null));
    }

    @DeleteMapping("/{predictionId}")
    @Operation(summary = "예측 결과 삭제", description = "특정 예측 결과를 삭제합니다")
    public ResponseEntity<ApiResponse<Void>> deletePrediction(
            @Parameter(description = "예측 ID") @PathVariable Long predictionId){
        log.info("예측 결과 삭제 요청 - ID: {}", predictionId);

        // 서비스 메서드 추가 필요
        // predictionService.deletePrediction(predictionId);

        return ResponseEntity.ok(ApiResponse.success("예측 결과가 성공적으로 삭제되었습니다", null));
    }
}
