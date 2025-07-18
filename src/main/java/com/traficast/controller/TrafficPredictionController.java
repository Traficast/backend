package com.traficast.controller;


import com.traficast.dto.request.PredictionRequest;
import com.traficast.dto.response.ApiResponse;
import com.traficast.dto.response.PredictionResponse;
import com.traficast.service.TrafficPredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/predictions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "교통량 예측 API", description = "교통량 예측 요청 및 결과 조회 관련 API")
public class TrafficPredictionController {

    private final TrafficPredictionService trafficPredictionService;

    /**
     * 교통량 예측 요청
     */
    @PostMapping
    @Operation(summary = "교통량 예측 요청", description = "지정된 위치와 시간에 대한 교통량 예측을 수행합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "예측 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류"
            )
    })
    public ResponseEntity<ApiResponse<List<PredictionResponse>>> predictTraffic(
            @Valid @RequestBody PredictionRequest request) {
        log.info("교통량 예측 요청 - 위치: {}, 날짜: {}",
                request.getLocationIds().size(), request.getTargetDatetime());

        List<PredictionResponse> predictions = trafficPredictionService.predictTraffic(request);

        return ResponseEntity.ok(ApiResponse.success("교통량 예측이 성공적으로 완료되었습니다.", predictions));
    }

    /**
     * 특정 위치의 최신 예측 결과 조회
     */
    @GetMapping("/locations/{locationId}/latest")
    @Operation(summary = "최신 예측 결과 조회", description = "특정 위치의 가장 최근 예측 결과를 조회합니다.")
    public ResponseEntity<ApiResponse<PredictionResponse>> getLatestPrediction(
            @Parameter(description = "위치 ID", required = true, example = "1")
            @PathVariable Long locationId){

        log.info("최신 예측 결과 조회 요청: 위치 ID {}", locationId);

        Optional<PredictionResponse> prediction = trafficPredictionService
                .getLatestPredictionForLocation(locationId);

        if(prediction.isPresent()){
            return ResponseEntity.ok(ApiResponse.success(
                    "최신 예측 결과를 성공적으로 조회했습니다.",
                    prediction.get()
            ));
        }else {
            return ResponseEntity.ok(ApiResponse.success(
                    "해당 위치의 예측 결과가 없습니다",
                    null
            ));
        }
    }

    /**
     * 예측 정확도 검증 트리거
     */
    @PostMapping("/validation/trigger")
    @Operation(summary = "예측 정확도 검증 실행", description = "예측 정확도 검증 작업을 수동으로 실행합니다.")
    public ResponseEntity<ApiResponse<String>> triggerAccuracyValidation(){

        log.info("예측 정확도 검증 수동 실행 요청");

        trafficPredictionService.validatePredictionAccuracy();

        return ResponseEntity.ok(ApiResponse.success(
                "예측 정확도 검증이 성공적으로 실행되었습니다.",
                "검증 작업이 백그라운드에서 실행됩니다."
        ));
    }
}


