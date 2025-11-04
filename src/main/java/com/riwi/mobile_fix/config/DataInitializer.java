package com.riwi.mobile_fix.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.riwi.mobile_fix.model.DeviceModel;
import com.riwi.mobile_fix.model.RepairOrder;
import com.riwi.mobile_fix.model.RepairStatus;
import com.riwi.mobile_fix.model.Role;
import com.riwi.mobile_fix.model.UserModel;
import com.riwi.mobile_fix.repository.DeviceRepository;
import com.riwi.mobile_fix.repository.RepairOrderRepository;
import com.riwi.mobile_fix.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final RepairOrderRepository repairOrderRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // Data already initialized
        }

        // Create default users
        UserModel admin = createUser("admin", "admin123", Role.ADMIN, "Alice Admin", "admin@mobilefix.com");
        UserModel technician = createUser("tech", "tech123", Role.TECH, "Tom Technician", "tech@mobilefix.com");
        UserModel customer = createUser("user", "user123", Role.USER, "Carol Customer", "user@mobilefix.com");

        // Save users first
        userRepository.saveAll(List.of(admin, technician, customer));

        // Create sample devices
        DeviceModel iphone = new DeviceModel();
        iphone.setBrand("Apple");
        iphone.setModel("iPhone 15 Pro");
        iphone.setSerialNumber("APL-IPH-15-001");

        DeviceModel galaxy = new DeviceModel();
        galaxy.setBrand("Samsung");
        galaxy.setModel("Galaxy S24");
        galaxy.setSerialNumber("SMG-S24-002");

        deviceRepository.saveAll(List.of(iphone, galaxy));

        // Create sample repair orders
        RepairOrder pendingOrder = new RepairOrder();
        pendingOrder.setCustomer(customer);
        pendingOrder.setDevice(iphone);
        pendingOrder.setIssueDescription("Screen cracked after accidental drop");
        pendingOrder.setStatus(RepairStatus.PENDING);

        RepairOrder inProgressOrder = new RepairOrder();
        inProgressOrder.setCustomer(customer);
        inProgressOrder.setDevice(galaxy);
        inProgressOrder.setIssueDescription("Battery drains quickly and phone overheats");
        inProgressOrder.setStatus(RepairStatus.IN_PROGRESS);
        inProgressOrder.setTechnician(technician);
        inProgressOrder.setTechNotes("Replacing battery and running diagnostics");

        repairOrderRepository.saveAll(List.of(pendingOrder, inProgressOrder));
    }

    private UserModel createUser(String username, String rawPassword, Role role, String fullName, String email) {
        UserModel user = new UserModel();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setActive(true);
        return user;
    }
}
