package com.traficast.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 모든 API 응답을 통일된 형태로 제공하는 공통 응답 DTO
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "API 공통 응답 형식")
public class ApiResponse<T>{

    @Schema(description = "응답 성공 여부", example = "true")
    private boolean success;

    @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다")
    private String message;

    @Schema(description = "응답 데이터")
    private T data;

    @Schema(description = "에러 코드(실패 시)", example = "INVAILD_PARAMETER")
    private String errorCode;

    @Schema(description = "에러 세부 정보(실패 시)")
    private Object errorDetails;

    @Builder.Default
    @Schema(description = "응답 시간", example = "2024-01-15T14:30:00")
    private LocalDateTime timestamp = LocalDateTime.now();

    @Schema(description = "요청 추적 ID", example = "req-123456789")
    private String traceId;

    // 성공 응답 생성 메서드
    public static <T> ApiResponse<T> success(T data){
        return ApiResponse.<T>builder()
                .success(true)
                .message("요청이 성공적으로 처리되었습니다")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    // 실패 응답 생성 메서드
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, String errorCode, Object errorDetails) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .errorDetails(errorDetails)
                .build();
    }
}
