package com.traficast.repository;

import com.traficast.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByActiveTrue();

    Optional<Location> findByNameAndActiveTrue(String name);

    @Query("SELECT l FROM Location l WHERE" +
           "l.latitude BETWEEN :minLat AND :maxLat AND " +
           "l.longtitude BETWEEN :minLng AND :maxLng AND " +
           "l.active = true")
    List<Location> findByLocations(
            @Param("lat") Double latitude,
            @Param("lng") Double longtitude,
            @Param("radius") Double radius
    );
}
