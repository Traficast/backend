package com.traficast.repository;


import com.traficast.entity.Location;
import com.traficast.entity.TrafficData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrafficDataRepository extends JpaRepository<TrafficData, Long> {

    List<TrafficData> findByLocationAndRecorededAtBetweenOrderByRecordedAt(
            Location location, LocalDateTime start, LocalDateTime end
    );

    @Query("SELECT td FROM TrafficData td WHERE td.location.id = :locationId " +
           "AND td.recordedAt BETWEEN :startDate AND :endDate " +
           "ORDER BY td.recordedAt DESC")
    List<TrafficData> findTrafficDataByLocationAndDataRange(
            @Param("locationId") Long locationId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT AVG(td.vehicleCount) FROM TrafficData td" +
           "WHERE td.location.id = :locationId " +
           "AND td.dayOfWeek = :dayOfWeek " +
           "AND td.hourOfDay = :hourOfDay")
    Double findAverageTrafficByLocationAndTime(
            @Param("locationId") Long locationId,
            @Param("dayOfWeek") Integer dayOfWeek,
            @Param("hourOfDay") Integer hourOfDay
    );

    @Query("SELECT td FROM TrafficData td WHERE td.location.id = :locationId " +
            "ORDER BY td.recordedAt DESC")
    List<TrafficData> findLatestTrafficDataByLocation(@Param("locationId") Long locationId);

    @Query("SELECT COUNT(td) FROM TrafficData td WHERE td.location.id = :locationId")
    Long countByLocationId(@Param("locationId") Long locationId);

    void deleteByLocationAndRecordedAtBefore(Location location, LocalDateTime cutoffDate);


}
