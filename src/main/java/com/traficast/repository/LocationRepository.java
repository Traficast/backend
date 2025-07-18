package com.traficast.repository;

import com.traficast.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// 위치 정보 관련 모든 데이터 접근을 담당(지리적 검색 + 기본 조회 기능 제공)
@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    /**
     * 위치명으로 검색(부분 일치, 대소문자 무시)
     * @param locationName 검색할 위치명
     * @return 해당 위치명이 포함된 Location 목록
     */
    List<Location> findByLocationNameContainingIgnoreCase(String locationName);

    /**
     * 정확한 위치명으로 검색
     * @param locationName 정확한 위치명
     * @return 해당 위치명의 Location (Optional)
     */
    Optional<Location> findByLocationName(String locationName);

    /**
     * 도로 유형별 위치 조회
     * @param roadType 도로 유형 (고속도로, 일반도로 등)
     * @return 해당 도로 유형의 Location 목록
     */
    List<Location> findByRoadTypeOrderByLocationName(String roadType);

    /**
     * 특정 반경 내 위치 조회(Haversine 공식 이용)
     * @param lat 중심 위도
     * @param lon 중심 경도
     * @param radius 반경(km)
     * @return 반경 내 Location 목록
     */
    @Query("SELECT I FROM Location I WHERE" +
            "6371 * acos(cos(radians(:lat)) * cos(radians(I.latitude)) * " +
            "cos(radians(I.longtitude) - radians(:lon)) + " +
            "sin(radians(:lat)) * sin(radians(I.latitude))) <= :radius")
    List<Location> findLocationsWithinRadius(@Param("lat") Double lat,
                                             @Param("lon") Double lon,
                                             @Param("radius") Double radius);


    /**
     * 삭제되지 않은 활성 위치만 조회
     * @return 활성 Location 목록
     */
    List<Location> findByIsDeleteFalseOrderByLocationName();

    /**
     * 최소 차선 수 이상의 위치 조회
     * @param minLaneCount 최소 차선 수
     * @return 조건에 맞는 Location 목록
     */
    List<Location> findByLaneCountGreaterThanEqualOrderByLaneCountDesc(Integer minLaneCount);

    // 추가 메서드
    List<Location> findByLocationNameContainingIgnoreCaseAndRoadType(String locationName, String roadType);
}
