package com.traficast.service;


import com.traficast.dto.request.DataUploadRequest;
import com.traficast.entity.Location;
import com.traficast.entity.TrafficData;
import com.traficast.repository.LocationRepository;
import com.traficast.repository.TrafficDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

// 교통 데이터 업로드 요청
@Service
@RequiredArgsConstructor
@Slf4j
public class DataUploadService {

    private final TrafficDataRepository trafficDataRepository;
    private final LocationRepository locationRepository;

    /**
     * 교통 데이터를 검증하고 일괄 업로드합니다.
     * @param request 업로드할 교통 데이터 요청 DTO
     * @return 저장된 TrafficData 엔티티 목록
     * @throws java.util.NoSuchElementException 위치 ID를 찾을 수 없는 경우
     * @throws IllegalArgumentException 데이터 검증 실패 시
     */
    @Transactional
    public List<TrafficData> uploadTrafficData(DataUploadRequest request){
        log.info("교통 데이터 업로드 요청 수신: {}개 항목, 출처: {}", request.getTrafficDataEntries().size(), request.getDataSource());

        // 1. 위치 ID 유효성 검증
        List<Long> locationIds = request.getTrafficDataEntries().stream()
                .map(DataUploadRequest.TrafficDataEntry::getLocationId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Location> locationMap = locationRepository.findAllById(locationIds)
                .stream()
                .collect(Collectors.toMap(Location::getId, location -> location));

        List<Long> missingLocationIds = locationIds.stream()
                .filter(id -> !locationMap.containsKey(id))
                .collect(Collectors.toList());

        if(!missingLocationIds.isEmpty()){
            throw new NoSuchElementException("다음 위치 ID를 찾을 수 없습니다: " + missingLocationIds);
        }

        // 2. 데이터 변환 및 저장
        List<TrafficData> savedTrafficData = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;

        for(DataUploadRequest.TrafficDataEntry entry : request.getTrafficDataEntries()){
            try{
                Location location = locationMap.get(entry.getLocationId());

                // 3. 데이터 무결성 검증
                validateTrafficDataEntry(entry);

                // 4. DTO -> Entity 변환
                TrafficData trafficData = TrafficData.builder()
                        .location(location)
                        .recordedAt(entry.getMeasuredAt())
                        .vehicleCount(entry.getVehicleCount())
                        .averageSpeed(entry.getAverageSpeed())
                        .congestionLevel(entry.getCongestionLevel())
                        .weatherCondition(entry.getWeatherCondition())
                        .temperature(entry.getTemperature())
                        .humidity(entry.getHumidity())
                        .visibility(entry.getVisibility())
                        .isHoliday(entry.getIsHoliday())
                        .dayOfWeek(entry.getDayOfWeek())
                        .hourOfDay(entry.getHourOfDay())
                        .build();

                savedTrafficData.add(trafficDataRepository.save(trafficData));
                successCount++;

                log.debug("교통 데이터 저장 완료: Location ID = {}, Measured At={}",
                        entry.getLocationId(), entry.getMeasuredAt());
            }catch (Exception e){
                errorCount++;
                log.error("교통 데이터 저장 실패: Location ID = {}, Error={}",
                        entry.getLocationId(), e.getMessage());
            }
        }

        log.info("교통 데이터 업로드 완료: 성공 {}개, 실패 {}", successCount, errorCount);
        return savedTrafficData;
    }

    /**
     * 교통 데이터 항목의 유효성을 검증합니다
     */
    private void validateTrafficDataEntry(DataUploadRequest.TrafficDataEntry entry){
        if(entry.getVehicleCount() < 0){
            throw new IllegalArgumentException("차량 수는 0이상이어야 합니다.");
        }

        if(entry.getAverageSpeed() != null && entry.getAverageSpeed() < 0){
            throw new IllegalArgumentException("평균 속도는 0 이상이어야 합니다.");
        }

        if(entry.getHourOfDay() != null && (entry.getHourOfDay() < 0 || entry.getHourOfDay() > 23))
        {
            throw new IllegalArgumentException("시간은 0-23 사이여야 합니다.");
        }
    }
}
