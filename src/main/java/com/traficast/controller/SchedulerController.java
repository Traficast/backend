package com.traficast.controller;


import com.traficast.dto.response.ApiResponse;
import com.traficast.service.SchedulerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/scheduler")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "스케줄러 관리 API", description = "배치 작업 수동 실행 및 관리 API(관리자용)")
public class SchedulerController {

    private final SchedulerService schedulerService;

    /**
     * 일일 예측 작업 수동 실행
     */
    @PostMapping("/daily-prediction")
    @Operation(summary = "일일 예측 수동 실행", description = "일일 교통량 예측 작업을 수동으로 실행합니다.")
    public ResponseEntity<ApiResponse<String>> triggerDailyPrediction(){
        log.info("일일 예측 작업 수동 실행 요청");

        schedulerService.dailyTrafficPredictionJob();

        return ResponseEntity.ok(ApiResponse.success(
                "일일 예측 작업이 성공적으로 실행되었습니다.",
                "작업이 백그라운드에서 수행됩니다."
        ));
    }

    /**
     * 시간별 예측 작업 수동 실행
     */
    @PostMapping("/hourly-prediction")
    @Operation(summary = "시간별 예측 수동 실행", description = "시간별 교통량 예측 작업을 수동으로 실행합니다.")
    public ResponseEntity<ApiResponse<String>> triggerHourlyPrediction(){
        log.info("시간별 예측 작업 수동 실행 요청");

        schedulerService.predictionAccuracyValidationJob();

        return ResponseEntity.ok(ApiResponse.success(
                "시간별 예측 작업이 성공적으로 실행되었습니다.",
                "작업이 백그라운드에서 실행됩니다."
        ));
    }

    /**
     * 예측 정확도 검증 수동 실행
     */
    @PostMapping("/accuracy-validation")
    @Operation(summary = "정확도 검증 수동 실행", description = "예측 정확도 검증 작업을 수동으로 실행합니다.")
    public ResponseEntity<ApiResponse<String>> triggerAccuracyValidation(){
        log.info("정확도 검증 작업 수동 실행 요청");

        schedulerService.predictionAccuracyValidationJob();

        return ResponseEntity.ok(ApiResponse.success(
                "정확도 검증 작업이 성공적으로 실행되었습니다.",
                "작업이 백그라운드에서 수행됩니다."
        ));
    }
}
