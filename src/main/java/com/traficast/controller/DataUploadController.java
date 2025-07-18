package com.traficast.controller;


import com.traficast.dto.request.DataUploadRequest;
import com.traficast.dto.response.ApiResponse;
import com.traficast.entity.TrafficData;
import com.traficast.service.DataUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/data")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "데이터 업로드 API", description = "교통 데이터 업로드 관련 API")
public class DataUploadController {

    private DataUploadService dataUploadService;

    /**
     * 교통 데이터 일괄 업로드
     */
    @PostMapping("/upload")
    @Operation(summary = "교통 데이터 업로드", description = "교통 데이터를 일괄적으로 업로드합니다.")
    public ResponseEntity<ApiResponse<String>> uploadTrafficData(@Valid @RequestBody DataUploadRequest request){

        log.info("교통 데이터 업로드 요청: {}개 항목, 출처: {}",
                request.getTrafficDataEntries().size(), request.getDataSource());

        List<TrafficData> uploadedData = dataUploadService.uploadTrafficData(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        String.format("총 %d개의 교통 데이터가 성공적으로 업로드되었습니다.", uploadedData.size()),
                        "업로드 완료"
                ));
    }
}
