package com.riwi.mobile_fix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.riwi.mobile_fix.model.RepairOrder;
import com.riwi.mobile_fix.model.RepairStatus;
import com.riwi.mobile_fix.model.UserModel;

public interface RepairOrderRepository extends JpaRepository<RepairOrder, Long> {
    List<RepairOrder> findByCustomer(UserModel customer);

    List<RepairOrder> findByStatus(RepairStatus status);

    List<RepairOrder> findByTechnician(UserModel technician);    
}
