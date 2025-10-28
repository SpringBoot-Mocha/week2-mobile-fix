package com.mobilefix.mobilefix.services;

import com.mobilefix.mobilefix.models.DeviceModel;
import com.mobilefix.mobilefix.repositories.DeviceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
    public class DeviceService {

        private final DeviceRepository deviceRepository;

        public DeviceService(DeviceRepository deviceRepository) {
            this.deviceRepository = deviceRepository;
        }

        // ✅ Crear un nuevo dispositivo
        public DeviceModel createDevice(DeviceModel device) {
            return deviceRepository.save(device);
        }

        // ✅ Obtener todos los dispositivos
        public List<DeviceModel> getAllDevices() {
            return deviceRepository.findAll();
        }

        // ✅ Buscar un dispositivo por id
        public Optional<DeviceModel> getDeviceById(int id) {
            return deviceRepository.findById(id);
        }

        // ✅ Actualizar un dispositivo
        public DeviceModel updateDevice(int id, DeviceModel deviceDetails) {
            DeviceModel device = deviceRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Device not found"));

            device.setBrand(deviceDetails.getBrand());
            device.setModel(deviceDetails.getModel());
            device.setSerialNumber(deviceDetails.getSerialNumber());
            return deviceRepository.save(device);
        }

        // ✅ Eliminar un dispositivo
        public void deleteDevice(int id) {
            deviceRepository.deleteById(id);
        }
    }


