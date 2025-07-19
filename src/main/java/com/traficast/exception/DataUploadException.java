package com.traficast.exception;


import lombok.Getter;

import java.util.List;

/**
 * 데이터 업로드 관련 비즈니스 로직에서 발생하는 커스텀 예외
 * 업로드 실패 상세 정보를 포함하여 디버깅과 사용자 피드백을 개선
 */
@Getter
public class DataUploadException extends  RuntimeException{

    private final int failedCount;
    private final int totalCount;
    private final List<String> errors;

    public DataUploadException(String message, int failedCount, int totalCount, List<String> errors){
        super(message);
        this.failedCount = failedCount;
        this.totalCount = totalCount;
        this.errors = errors;
    }

    public DataUploadException(String message, int failedCount, int totalCount){
        super(message);
        this.failedCount = failedCount;
        this.totalCount = totalCount;
        this.errors = List.of();
    }

    /**
     * 부분 실패 상황을 나타내는 정적 패토리 메서드
     */
    public static DataUploadException partialFailure(int failedCount, int totalCount, List<String> errors){
        return new DataUploadException(
                String.format("데이터 업로드 부분 실패: 전체 %d개 중 %d개 실패", totalCount, failedCount),
                failedCount,
                totalCount,
                errors
        );
    }

    /**
     * 완전 실패 상황을 나타내는 정적 팩토리 메서드
     */
    public static DataUploadException completeFailure(int totalCount, String reason){
        return new DataUploadException(
                String.format("데이터 업로드 완전 실패: %s", reason),
                totalCount,
                totalCount,
                List.of(reason)
        );
    }
}
