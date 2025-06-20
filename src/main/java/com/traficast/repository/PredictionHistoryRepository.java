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

@Repository
public interface PredictionHistoryRepository extends JpaRepository<PredictionHistory, Long> {

    List<PredictionHistory> findByLocationAndPredictionDateBetweenOrderByPredictionDateDesc(
            Location location, LocalDateTime start, LocalDateTime end
    );

    Optional<PredictionHistory> findByLocationAndPredictionDate(
            Location location, LocalDateTime predictDate
    );

    @Query("SELECT ph FROM PredictHistory ph WHERE ph.location.id = :locationId" +
           "ORDER BY ph.predictionDate DESC")
    Page<PredictionHistory> findLatestPredictionByLocation(
            @Param("locationId") Long locationId, Pageable pageable
    );

    @Query("SELECT AVG(ph.accuracyScore) FROM PredictionHistory ph " +
           "WHERE ph.location.id = :locationId " +
           "AND ph.accuracyScore IS NOT NULL")
    Double findAverageAccuracyByLocation(@Param("locationId") Long locationId);

    @Query("SELECT ph FROM PredictionHistory ph WHERE ph.predictionDate < :cutoffDate")
    List<PredictionHistory> findOldPredctions(@Param("cutoffDate") LocalDateTime cutoffDate);

    void deleteByPredictionDateBefore(LocalDateTime cutoffDate);
}
