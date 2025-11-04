package com.riwi.mobile_fix.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.riwi.mobile_fix.dto.CreateDeviceRequest;
import com.riwi.mobile_fix.model.DeviceModel;
import com.riwi.mobile_fix.service.DeviceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping
    public ResponseEntity<List<DeviceModel>> getAllDevices(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model) {
        
        if (brand != null) {
            return ResponseEntity.ok(deviceService.searchByBrand(brand));
        }
        
        if (model != null) {
            return ResponseEntity.ok(deviceService.searchByModel(model));
        }
        
        return ResponseEntity.ok(deviceService.getAllDevices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceModel> getDeviceById(@PathVariable Long id) {
        DeviceModel device = deviceService.getDeviceById(id);
        return ResponseEntity.ok(device);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeviceModel> createDevice(@Valid @RequestBody CreateDeviceRequest request) {
        DeviceModel device = deviceService.createDevice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(device);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeviceModel> updateDevice(
            @PathVariable Long id,
            @Valid @RequestBody CreateDeviceRequest request) {
        DeviceModel device = deviceService.updateDevice(id, request);
        return ResponseEntity.ok(device);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.ok(Map.of("message", "Device deleted successfully"));
    }
}
