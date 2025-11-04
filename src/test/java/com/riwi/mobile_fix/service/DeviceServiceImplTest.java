package com.riwi.mobile_fix.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.riwi.mobile_fix.dto.CreateDeviceRequest;
import com.riwi.mobile_fix.exception.ConflictException;
import com.riwi.mobile_fix.exception.ResourceNotFoundException;
import com.riwi.mobile_fix.model.DeviceModel;
import com.riwi.mobile_fix.repository.DeviceRepository;
import com.riwi.mobile_fix.service.impl.DeviceServiceImpl;

@ExtendWith(MockitoExtension.class)
class DeviceServiceImplTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceServiceImpl deviceService;

    @Test
    void createDevice_SerialAlreadyExists_ShouldThrowConflict() {
        CreateDeviceRequest request = new CreateDeviceRequest("Apple", "iPhone", "SN123");
        DeviceModel existing = new DeviceModel();
        existing.setId(1L);

        when(deviceRepository.findBySerialNumber("SN123")).thenReturn(existing);

        assertThrows(ConflictException.class, () -> deviceService.createDevice(request));
    }

    @Test
    void createDevice_ShouldPersistDevice() {
        CreateDeviceRequest request = new CreateDeviceRequest("Apple", "iPhone", "SN124");
        DeviceModel saved = new DeviceModel();
        saved.setId(5L);
        saved.setBrand("Apple");
        saved.setModel("iPhone");
        saved.setSerialNumber("SN124");

        when(deviceRepository.findBySerialNumber("SN124")).thenReturn(null);
        when(deviceRepository.save(any(DeviceModel.class))).thenReturn(saved);

        DeviceModel result = deviceService.createDevice(request);

        assertEquals(saved.getId(), result.getId());
        verify(deviceRepository).save(any(DeviceModel.class));
    }

    @Test
    void updateDevice_SerialConflict_ShouldThrowConflict() {
        CreateDeviceRequest request = new CreateDeviceRequest("Apple", "iPhone", "SN200");
        DeviceModel target = new DeviceModel();
        target.setId(10L);
        DeviceModel other = new DeviceModel();
        other.setId(11L);

        when(deviceRepository.findById(10L)).thenReturn(Optional.of(target));
        when(deviceRepository.findBySerialNumber("SN200")).thenReturn(other);

        assertThrows(ConflictException.class, () -> deviceService.updateDevice(10L, request));
    }

    @Test
    void getDeviceById_NotFound_ShouldThrow() {
        when(deviceRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> deviceService.getDeviceById(999L));
    }

    @Test
    void deleteDevice_ShouldInvokeRepositoryDelete() {
        DeviceModel device = new DeviceModel();
        device.setId(22L);

        when(deviceRepository.findById(22L)).thenReturn(Optional.of(device));

        deviceService.deleteDevice(22L);

        verify(deviceRepository).delete(device);
    }

    @Test
    void searchByBrand_ShouldDelegateToRepository() {
        when(deviceRepository.findByBrandContainingIgnoreCase("apple")).thenReturn(List.of());
        deviceService.searchByBrand("apple");
        verify(deviceRepository).findByBrandContainingIgnoreCase("apple");
    }
}
