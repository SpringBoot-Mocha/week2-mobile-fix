package com.mobilefix.mobilefix.repositories;

import com.mobilefix.mobilefix.models.DeviceModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<DeviceModel, Integer> {

    java.util.List<DeviceModel> findByBrand(String brand);
    DeviceModel findBySerialNumber(String serialNumber);

}

