package com.traficast.service;


import com.traficast.dto.DtoMapper;
import com.traficast.dto.request.LocationRequest;
import com.traficast.dto.response.LocationResponse;
import com.traficast.entity.Location;
import com.traficast.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {

    private final LocationRepository locationRepository;
    private final DtoMapper dtoMapper;

    @Transactional
    public LocationResponse createLocation(LocationRequest request){
        Location location = Location.builder()
                .locationName(request.getLocationName())
                .address(request.getAddrss())
                .latitude(request.getLatitude())
                .longitude(request.getLatitude())
                .roadType(request.getRoadType())
                .laneCount(request.getLaneCount())
                .speedLimit(request.getSpeedLimit())
                .description(request.getDescription())
                .build();

        Location savedLocation = locationRepository.save(location);
        return dtoMapper.toLocationResponse(savedLocation);
    }

    public LocationResponse getLocationById(Long locationId){
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new NoSuchElementException("위치 ID" + locationId + "를 찾을 수 없습니다."));

        return dtoMapper.toLocationResponse(location);
    }

    public List<LocationResponse> getLocations(String keyword, String roadType){
        List<Location> locations;

        if(keyword != null && roadType != null){
            locations = locationRepository.findByLocationNameContainingIgnoreCaseAndRoadType(keyword, roadType);
        }else if(keyword != null){
            locations = locationRepository.findByLocationNameContainingIgnoreCase(keyword);
        }else if(roadType != null){
            locations = locationRepository.findByRoadTypeOrderByLocationName(roadType);
        }else{
            locations = locationRepository.findByIsDeleteFalseOrderByLocationName();
        }

        return locations.stream()
                .map(dtoMapper::toLocationResponse)
                .collect(Collectors.toList());
    }

    public List<LocationResponse> getNearbyLocations(Double latitude, Double longitude, Double radius){
        List<Location> locations = locationRepository.findLocationsWithinRadius(latitude, longitude, radius);
        return locations.stream()
                .map(dtoMapper::toLocationResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public LocationResponse updateLocation(Long locationId, LocationRequest request){
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new NoSuchElementException("위치 ID" + locationId + "를 찾을 수 없습니다."));

        location.setLocationName(request.getLocationName());
        location.setAddress(request.getAddrss());
        location.setLatitude(request.getLatitude());
        location.setRoadType(request.getRoadType());
        location.setLongitude(request.getLongitude());
        location.setLaneCount(request.getLaneCount());
        location.setSpeedLimit(request.getSpeedLimit());
        location.setDescription(request.getDescription());

        Location updateLocation = locationRepository.save(location);
        return dtoMapper.toLocationResponse(updateLocation);
    }

    @Transactional
    public void deleteLocation(Long locationId){
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new NoSuchElementException("위치 ID"  + locationId + "를 찾을 수 없습니다."));

        location.setIsDeleted(true);
        locationRepository.save(location);
    }
}
