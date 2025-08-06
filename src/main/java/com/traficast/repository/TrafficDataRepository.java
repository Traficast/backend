package com.traficast.repository;


import com.traficast.entity.Location;
import com.traficast.entity.TrafficData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrafficDataRepository extends JpaRepository<TrafficData, Long> {

    /**
     * 특정 위치의 최신 교통 데이터 조회
     * @param location 조회할 위치
     * @return 최신 교통 데이터(Optional)
     */
    Optional<TrafficData> findTopByLocationOrderByRecordedAtDesc(Location location);

    /**
     * 특정 위치의 기간별 교통 데이터 조회
     * @param location 조회할 위치
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @return 기간 내 교통 데이터
     */
    List<TrafficData> findByLocationAndRecordedAtBetweenOrderByRecordedAtDesc(
            Location location, LocalDateTime startTime, LocalDateTime endTime
    );

    /**
     * 페이징을 통한 교통 데이터 조회
     * @param location 조회할 위치
     * @param pageable 페이징 정보
     * @return 페이징된 교통 데이터
     */
    Page<TrafficData> findByLocationOrderByRecordedAtDesc(Location location, Pageable pageable);

    /**
     * 특정 혼잡도 이상의 데이터 조회
     * @param congestionLevels 혼잡도 목록
     * @return 해당 혼잡도의 교통 데이터 목록
     */
    List<TrafficData> findByCongestionLevelInOrderByRecordedAtDesc(
            List<TrafficData.CongestionLevel> congestionLevels
    );

    /**
     * 시간대별 평균 교통량 조회(통계용)
     * @param location 조회할 위치
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return [시간 대, 평균차량 수, 평균 속도] 배열 목록
     */

    @Query("SELECT td.hourOfDay, AVG(td.vehicleCount), AVG(td.averageSpeed) " +
            "FROM TrafficData td " +
            "WHERE td.location = :location " +
            "AND td.recordedAt BETWEEN :startDate AND :endDate " +
            "GROUP BY td.hourOfDay " +
            "ORDER BY td.hourOfDay")
    List<Object[]> findHourlyAverageByLocationAndDateRange(
            @Param("location") Location location,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * 요일별 평균 교통량 조회
     * @param location 조회할 위치
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return [요일, 평균차량수, 평균속도] 배열 목록
     */
    @Query("SELECT td.dayOfWeek, AVG(td.vehicleCount), AVG(td.averageSpeed) " +
            "FROM TrafficData td " +
            "WHERE td.location = :location " +
            "AND td.recordedAt BETWEEN :startDate AND :endDate " +
            "GROUP BY td.dayOfWeek " +
            "ORDER BY td.dayOfWeek")
    List<Object[]> findWeeklyAverageByLocationAndDateRange(
            @Param("location") Location location,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * 날씨별 교통 패턴 분석
     * @param location 조회할 위치
     * @return [날씨상태, 평균차량수, 평균속도] 배열 목록
     */
    @Query("SELECT td.weatherCondition, AVG(td.vehicleCount), AVG(td.averageSpeed) " +
            "FROM TrafficData td " +
            "WHERE td.location = :location " +
            "AND td.weatherCondition IS NOT NULL " +
            "GROUP BY td.weatherCondition")
    List<Object[]> findTrafficPatternsByWeather(
            @Param("location") Location location
    );

    /**
     * 머신러닝 모델 학습용 데이터 조회
     * @param location 조회할 위치
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 학습용 교통 데이터 목록
     */

    @Query("SELECT td FROM TrafficData td " +
            "WHERE td.location = :location " +
            "AND td.recordedAt BETWEEN :startDate AND :endDate " +
            "AND td.vehicleCount IS NOT NULL " +
            "AND td.averageSpeed IS NOT NULL " +
            "ORDER BY td.recordedAt")
    List<TrafficData> findTrainingDataByLocationAndDataRange(
            @Param("location") Location location,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


}
