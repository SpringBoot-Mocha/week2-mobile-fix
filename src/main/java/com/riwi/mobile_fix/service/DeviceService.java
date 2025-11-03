package com.riwi.mobile_fix.service;

import java.util.List;

import com.riwi.mobile_fix.dto.CreateDeviceRequest;
import com.riwi.mobile_fix.model.DeviceModel;

public interface DeviceService {
    
    DeviceModel createDevice(CreateDeviceRequest request);
    
    List<DeviceModel> getAllDevices();
    
    DeviceModel getDeviceById(Long id);
    
    DeviceModel updateDevice(Long id, CreateDeviceRequest request);
    
    void deleteDevice(Long id);
    
    List<DeviceModel> searchByBrand(String brand);
    
    List<DeviceModel> searchByModel(String model);
}
