package com.traficast.exception;


import com.traficast.entity.PredictionHistory;

import java.util.List;

/**
 * 예측 관련 비즈니스 로직에서 발생하는 커스텀 예외
 * 모델 통신, 예측 데이터 처리 등에서 발생하는 오류 상세 정보 포함
 */
public class PredictionException extends RuntimeException{

    private final List<Long> locationIds;
    private final PredictionHistory.PredictionType predictionType;
    private final String modelVersion;
    private final String errorCode;

    public PredictionException(String message, List<Long> locationIds,
                               PredictionHistory.PredictionType predictionType,
                               String modelVersion, String errorCode){
        super(message);
        this.locationIds = locationIds;
        this.predictionType = predictionType;
        this.modelVersion = modelVersion;
        this.errorCode = errorCode;
    }

    public PredictionException(String message, List<Long> locationIds,
                               PredictionHistory.PredictionType predictionType){
        super(message);
        this.locationIds = locationIds;
        this.predictionType = predictionType;
        this.modelVersion = "unknown";
        this.errorCode = "PREDICTION_FAILED";
    }

    // 수동 getter 메서드 추가 (Lombok 문제 해결)
    public List<Long> getLocationIds() {
        return locationIds;
    }

    public PredictionHistory.PredictionType getPredictionType() {
        return predictionType;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public String getErrorCode() {
        return errorCode;
    }

    /**
     * 모델 API 통신 실패
     */
    public static PredictionException modelApiFailure(List<Long> locationIds,
                                                      PredictionHistory.PredictionType predictionType,
                                                      String modelVersion, String reason){
        return new PredictionException(
                String.format("모델 API 통신 실패: %s", reason),
                locationIds,
                predictionType,
                modelVersion,
                "MODEL_API_ERROR"
        );
    }

    /**
     * 예측 데이터 검증 실패
     */
    public static PredictionException validationFailure(List<Long> locationIds,
                                                        PredictionHistory.PredictionType predictionType,
                                                        String reason){
        return new PredictionException(
                String.format("예측 요청 검증 실패: %s", reason),
                locationIds,
                predictionType,
                "unknown",
                "VALIDATION ERROR"
        );
    }

    /**
     * 예측 결과 처리 실패
     */
    public static PredictionException processingFailure(List<Long> locationIds,
                                                        PredictionHistory.PredictionType predictionType,
                                                        String modelVersion, String reason){
        return new PredictionException(
                String.format("예측 결과 처리 실패: %s", reason),
                locationIds,
                predictionType,
                modelVersion,
                "PROCESSING_ERROR"
        );
    }
}
