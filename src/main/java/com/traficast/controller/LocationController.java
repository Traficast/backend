package com.traficast.controller;


import com.traficast.dto.request.LocationRequest;
import com.traficast.dto.response.ApiResponse;
import com.traficast.dto.response.LocationResponse;
import com.traficast.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "위치 관리 API", description = "교통량 측정 위치 관련 API")
public class LocationController {

    private final LocationService locationService;

    /**
     * 새로운 위치 등록
     */
    @PostMapping
    @Operation(summary = "위치 등록", description = "새로운 교통량 측정 위치를 등록합니다.")
    public ResponseEntity<ApiResponse<LocationResponse>> createLocation(
            @Valid @RequestBody LocationRequest request){
        log.info("새로운 위치 등록 요청: {}", request.getLocationName());

        LocationResponse location = locationService.createLocation(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("위치가 성공적으로 등록되었습니다.", location));
    }

    /**
     * 위치 상세 정보 조회
     */
    @GetMapping("/{locationId}")
    @Operation(summary = "위치 상세 조회", description = "특정 위치의 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<LocationResponse>> getLocation(
            @Parameter(description = "위치 ID", required = true, example = "1")
            @PathVariable Long locationId){
        log.info("위치 상세 조회 요청: ID {}", locationId);

        LocationResponse location = locationService.getLocationById(locationId);

        return ResponseEntity.ok(ApiResponse.success(
                "위치 정보를 성공적으로 조회했습니다.",
                location
        ));
    }

    /**
     * 위치 목록 조회
     */
    @GetMapping
    @Operation(summary = "위치 목록 조회", description = "등록된 위치 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> getLocations(
            @Parameter(description = "검색 키워드(위치명)", example = "강남역")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "도로 유형", example = "시내도로")
            @RequestParam(required = false) String roadType
    ){
        log.info("위치 목록 조회: keyword= {}, roadType= {}", keyword, roadType);

        List<LocationResponse> locations = locationService.getLocations(keyword, roadType);

        return ResponseEntity.ok(ApiResponse.success(
                "위치 목록을 성공적으로 조회했습니다",
                locations
        ));
    }

    /**
     * 특정 반경 내 위치 검색
     */
    @GetMapping("/nearby")
    @Operation(summary = "주변 위치 검색", description = "지정된 좌표 주변의 위치를 검색합니다.")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> getNearbyLocations(
            @Parameter(description = "중심 위도", required = true, example = "37.4979")
            @RequestParam Double latitude,

            @Parameter(description = "중심 경도", required = true, example = "127.0276")
            @RequestParam Double longitude,

            @Parameter(description = "검색 반경(km)", example = "5.0")
            @RequestParam(defaultValue = "5.0") Double radius){

        log.info("주변 위치 검색: lat={}, lon={}, radius={}km", latitude, longitude, radius);

        List<LocationResponse> locations = locationService.getNearbyLocations(latitude, longitude, radius);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("반경 %.1km 내 위치 %d개를 찾았습니다.", radius, locations.size()),
                locations
        ));
    }

    /**
     * 위치 정보 수정
     */
    @PutMapping("/{locationId}")
    @Operation(summary = "위치 정보 수정", description = "기존 위치 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<LocationResponse>> updateLocation(
            @Parameter(description = "위치 ID", required = true, example = "1")
            @PathVariable Long locationId,
            @Valid @RequestBody LocationRequest request
    ){
        log.info("위치 정보 수정 요청: ID {}, 이름 {}", locationId, request.getLocationName());

        LocationResponse location = locationService.updateLocation(locationId, request);

        return ResponseEntity.ok(ApiResponse.success(
                "위치 정보가 성공적으로 수정되었습니다.",
                location
        ));
    }

    /**
     * 위치 삭제(소프트 삭제)
     */
    @DeleteMapping("/{locationId}")
    @Operation(summary = "위치 삭제", description = "특정 위치를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteLocation(
            @Parameter(description = "위치 ID", required = true, example = "1")
            @PathVariable Long locationId
    ){
        log.info("위치 삭제 요청: ID {}", locationId);

        locationService.deleteLocation(locationId);

        return ResponseEntity.ok(ApiResponse.success(
                "위치가 성공적으로 삭제되었습니다.",
                null
        ));
    }
}
