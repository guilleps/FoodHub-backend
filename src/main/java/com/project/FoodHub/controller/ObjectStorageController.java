package com.project.FoodHub.controller;

import com.project.FoodHub.service.UploadImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/object-storage")
@RequiredArgsConstructor
public class ObjectStorageController {
    private final UploadImageService uploadImageService;

    @GetMapping("/health")
    public ResponseEntity<?> storageHealth() {
        if (uploadImageService.isAvailable()) {
            return ResponseEntity.ok(
                    Map.of(
                            "status", "UP",
                            "message", "Object storage service is available."
                    )
            );
        } else {
            return ResponseEntity.status(503).body(
                    Map.of(
                            "status", "DOWN",
                            "message", "Object storage service is not available."
                    )
            );
        }
    }
}
