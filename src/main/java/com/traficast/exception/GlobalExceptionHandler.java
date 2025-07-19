package com.traficast.exception;


import com.traficast.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 전역 예외 처리 핸들러
 * 모든 Controller 에서 발생하는 예외를 일관된 ApiResponse 형태로 처리
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 유효성 검증 실패 예외 처리(@Valid 어노테이션 실패 시)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request){

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError)error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("유효성 검증 실패: {} | 요청: {}", errors, request.getDescription(false));

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>> builder()
                .success(false)
                .message("요청 데이터 유효성 검증에 실패했습니다.")
                .errorCode("VALIDATION_ERROR")
                .errorDetails(errors)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 리소스 없음 예외 처리(findById().orElseThrow() 등)
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoSuchElementException(
            NoSuchElementException ex, WebRequest request
    ){
        log.warn("리소스 없음 예외: {} | 요청: {}", ex.getMessage(), request.getDescription(false));

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .message(ex.getMessage())
                .errorCode("RESOURCE_NOT_FOUND")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 잘못된 인자 예외 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request){
        log.error("잘못된 인자 예외: {} | 요청: {}", ex.getMessage(), request.getDescription(false));

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .message(ex.getMessage())
                .errorCode("INVALID_ARGUMENT")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 데이터 업로드 관련 예외 처리
     */
    @ExceptionHandler(DataUploadException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleDataUploadException(
            DataUploadException ex, WebRequest request
    ){
        log.error("데이터 업로드 관련 예외: {} | 요청: {}", ex.getMessage(), request.getDescription(false));

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("failedCount", ex.getFailedCount());
        errorDetails.put("totalCount", ex.getTotalCount());
        errorDetails.put("errors", ex.getErrors());

        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .success(false)
                .message(ex.getMessage())
                .errorCode("DATA_UPLOAD_ERROR")
                .errorDetails(errorDetails)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 예측 관련 예외 처리
     */
    @ExceptionHandler(PredictionException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handlePredictionException(
            PredictionException ex, WebRequest request
    ){
        log.error("예측 예외: {} | 요청: {}", ex.getMessage(), request.getDescription(false));

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("predctionType", ex.getPredictionType());
        errorDetails.put("locationIds", ex.getLocationIds());
        errorDetails.put("modelVersion", ex.getModelVersion());

        ApiResponse<Map<String,Object>> response = ApiResponse.<Map<String, Object>>builder()
                .success(false)
                .message(ex.getMessage())
                .errorCode("PREDICTION_ERROR")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 일반적인 런타임 예외 처리
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(
            RuntimeException ex, WebRequest request){
        log.error("런타임 예외 발생: {} | 요청: {}", ex.getMessage(), request.getDescription(false), ex);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .message("서버 내부 오류가 발생했습니다.")
                .errorCode("INTERNAL_SERVER_ERROR")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 예상치 못한 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex, WebRequest request){
        log.error("예상치 못한 예외 발생: {} | 요청: {}", ex.getMessage(),request.getDescription(false) ,ex);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .message("예상치 못한 오류가 발생했습니다.")
                .errorCode("UNKNOWN_ERROR")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
