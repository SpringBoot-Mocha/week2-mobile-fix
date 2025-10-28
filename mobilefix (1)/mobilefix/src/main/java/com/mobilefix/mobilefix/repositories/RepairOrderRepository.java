package com.mobilefix.mobilefix.repositories;

import com.mobilefix.mobilefix.models.RepairOrderModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepairOrderRepository extends JpaRepository<RepairOrderModel, Integer> {
    List<RepairOrderModel> findByStatus(String status);

    // buscar ordenes asignadas a un técnico específico
    List<RepairOrderModel> findByAssignedTech(String assignedTech);

    // buscar óodenes por nombre del cliente
    List<RepairOrderModel> findByCustomer(String customer);

}

