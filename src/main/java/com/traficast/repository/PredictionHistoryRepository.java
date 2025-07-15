package com.traficast.repository;


import com.traficast.entity.Location;
import com.traficast.entity.PredictionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// 예측 결과 조회와 정확도 분석을 위한 고급 기능
@Repository
public interface PredictionHistoryRepository extends JpaRepository<PredictionHistory, Long> {

    /**
     * 특정 위치의 최신 예측 결과 조회
     * @param location 조회할 위치
     * @return 최신 예측 결과 (Optional)
     */
    Optional<PredictionHistory> findTopByLocationOrderByPredictionTimeDesc(Location location);

    /**
     * 특정 위치의 기간별 예측 기록 조회
     * @param location 조회할 위치
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @return 기간 내 예측 기록 목록
     */
    List<PredictionHistory> findByLocationAndPredictionTimeBetweenOrderByPredictionTimeDesc(
            Location location, LocalDateTime startTime, LocalDateTime endTime
    );

    /**
     * 예측 타입별 기록 조회
     * @param location 조회할 위치
     * @param predictionType 예측 타입
     * @return
     */
    List<PredictionHistory> findByLocationAndPredictionTypeOrderByPredictionTimeDesc(
            Location location, PredictionHistory.PredictionType predictionType
    );

    /**
     * 특정 모델 버전의 예측 기록 조회
     * @param modelVersion 모델 버전
     * @return 해당 모델 버전의 예측 기록 목록
     */
    List<PredictionHistory> findByModelVersionOrderByPredictionTimeDesc(String modelVersion);

    /**
     * 예측 정확도 분석(실제값이 있는 경우만)
     * @param location 조회할 위치
     * @return 검증된 예측 기록
     */
    @Query("SELECT ph FROM PredictionHistory ph " +
            "WHERE ph.location = :location " +
            "AND ph.actualVehicleCount IS NOT NULL " +
            "AND ph.accuracyScore IS NOT NULL " +
            "ORDER BY ph.predictionTime DESC")
    List<PredictionHistory> findValidatedPredictions(@Param("location") Location location);

    /**
     * 모델별 평균 정확도 조회
     * @return 해당 모델의 예측 기록 목록
     */
    @Query("SELECT ph.modelVersion, AVG(ph.accuracyScore), COUNT(ph) " +
            "FROM PredictionHistory ph " +
            "WHERE ph.accuracyScore IS NOT NULL" +
            "GROUP BY ph.modelVersion" +
            "ORDER BY AVG(ph.accuracyScore) DESC")
    List<Object[]> findModelAccuracyStatistics();

    /**
     * 미래 예측 결과 조회(아직 실제값이 없는 예측들)
     * @param location 조회할 위치
     * @param currentTime 현재 시간
     * @return 미래 예측 목록
     */
    @Query("SELECT ph FROM PredictionHistory ph " +
            "WHERE ph.location = :location " +
            "AND ph.targetDatetime > :currentTime " +
            "ORDER BY ph.targetDateTime")
    List<PredictionHistory> findFuturePredictions(
            @Param("location") Location location,
            @Param("currentTime") LocalDateTime currentTime
    );

    /**
     * 특정 신뢰도 이상의 예측만 조회
     * @param location 조회할 위치
     * @param minConfidenceScore 최소 신뢰도
     * @return 신뢰도 조건에 맞는 예측 목록
     */
    List<PredictionHistory> findByLocationAndConfidenceScoreGreaterThanEqualOrderByPredictionTimeDesc(
            Location location, Double minConfidenceScore
    );

    /**
     * 페이징을 통한 예측 기록 조회
     * @param location 조회할 위치
     * @param pageable 페이징 정보
     * @return 페이징된 예측 기록
     */
    Page<PredictionHistory> findByLocationOrderByPredictionTimeDesc(Location location, Pageable pageable);

    /**
     * 특정 기간 내 예측 결과 중 아직 실제값이 검증되지 않은 예측들을 조회
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @return 미검증 예측 목록
     */
    List<PredictionHistory> findByPredctionTimeBetweenAndActualVehicleCountIsNull(
            LocalDateTime startTime, LocalDateTime endTime
    );
}
