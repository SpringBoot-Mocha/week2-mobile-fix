package com.riwi.mobile_fix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.riwi.mobile_fix.model.DeviceModel;

@Repository
public interface DeviceRepository extends JpaRepository<DeviceModel, Long> {
    List<DeviceModel> findByBrandContainingIgnoreCase(String brand);
    List<DeviceModel> findByModelContainingIgnoreCase(String model);
    DeviceModel findBySerialNumber(String serialNumber);
}
