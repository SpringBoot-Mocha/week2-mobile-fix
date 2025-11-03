package com.riwi.mobile_fix.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.riwi.mobile_fix.dto.CreateDeviceRequest;
import com.riwi.mobile_fix.exception.ConflictException;
import com.riwi.mobile_fix.exception.ResourceNotFoundException;
import com.riwi.mobile_fix.model.DeviceModel;
import com.riwi.mobile_fix.repository.DeviceRepository;
import com.riwi.mobile_fix.service.DeviceService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;

    @Override
    public DeviceModel createDevice(CreateDeviceRequest request) {
        // Check if serial number already exists
        if (request.getSerialNumber() != null && !request.getSerialNumber().isEmpty()) {
            DeviceModel existing = deviceRepository.findBySerialNumber(request.getSerialNumber());
            if (existing != null) {
                throw new ConflictException("Device with serial number " + request.getSerialNumber() + " already exists");
            }
        }

        DeviceModel device = new DeviceModel();
        device.setBrand(request.getBrand());
        device.setModel(request.getModel());
        device.setSerialNumber(request.getSerialNumber());

        return deviceRepository.save(device);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceModel> getAllDevices() {
        return deviceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceModel getDeviceById(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + id));
    }

    @Override
    public DeviceModel updateDevice(Long id, CreateDeviceRequest request) {
        DeviceModel device = getDeviceById(id);

        // Check if serial number conflicts with another device
        if (request.getSerialNumber() != null && !request.getSerialNumber().isEmpty()) {
            DeviceModel existing = deviceRepository.findBySerialNumber(request.getSerialNumber());
            if (existing != null && !existing.getId().equals(id)) {
                throw new ConflictException("Device with serial number " + request.getSerialNumber() + " already exists");
            }
        }

        device.setBrand(request.getBrand());
        device.setModel(request.getModel());
        device.setSerialNumber(request.getSerialNumber());

        return deviceRepository.save(device);
    }

    @Override
    public void deleteDevice(Long id) {
        DeviceModel device = getDeviceById(id);
        deviceRepository.delete(device);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceModel> searchByBrand(String brand) {
        return deviceRepository.findByBrandContainingIgnoreCase(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceModel> searchByModel(String model) {
        return deviceRepository.findByModelContainingIgnoreCase(model);
    }
}
