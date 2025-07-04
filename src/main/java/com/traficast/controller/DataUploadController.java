package com.traficast.controller;


import com.traficast.dto.request.DataUploadRequest;
import com.traficast.service.DataUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/data")
public class DataUploadController {

    @Autowired
    private DataUploadService dataUploadService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadData(@RequestBody DataUploadRequest request){
        // Implementation for data upload
        return ResponseEntity.ok("Data uploaded successfully");
    }
}
