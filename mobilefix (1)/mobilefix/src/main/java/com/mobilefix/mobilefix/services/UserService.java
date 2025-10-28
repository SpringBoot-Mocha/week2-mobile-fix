package com.mobilefix.mobilefix.services;

import com.mobilefix.mobilefix.models.RepairOrderModel;
import com.mobilefix.mobilefix.repositories.RepairOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private RepairOrderRepository repairOrderRepository;

    // 🔹 Listar todas las órdenes
    public List<RepairOrderModel> getAllOrders() {
        return repairOrderRepository.findAll();
    }

    // 🔹 Crear una nueva orden (añadiendo fecha automáticamente)
    public RepairOrderModel createOrder(RepairOrderModel order) {
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("Pendiente");
        return repairOrderRepository.save(order);
    }

    // 🔹 Buscar por ID
    public RepairOrderModel getOrderById(int id) {
        return repairOrderRepository.findById(id).orElse(null);
    }

    // 🔹 Actualizar el estado o información de una orden
    public RepairOrderModel updateOrder(int id, RepairOrderModel orderDetails) {
        RepairOrderModel existing = getOrderById(id);
        if (existing != null) {
            existing.setStatus(orderDetails.getStatus());
            existing.setTechNotes(orderDetails.getTechNotes());
            existing.setAssignedTech(orderDetails.getAssignedTech());
            existing.setUpdatedAt(LocalDateTime.now());
            return repairOrderRepository.save(existing);
        }
        return null;
    }

    // 🔹 Eliminar una orden
    public boolean deleteOrder(int id) {
        if (repairOrderRepository.existsById(id)) {
            repairOrderRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 🔹 Buscar órdenes por estado
    public List<RepairOrderModel> getOrdersByStatus(String status) {
        return repairOrderRepository.findByStatus(status);
    }

    // 🔹 Buscar órdenes asignadas a un técnico
    public List<RepairOrderModel> getOrdersByTech(String tech) {
        return repairOrderRepository.findByAssignedTech(tech);
    }

    // 🔹 Buscar órdenes por cliente
    public List<RepairOrderModel> getOrdersByCustomer(String customer) {
        return repairOrderRepository.findByCustomer(customer);
    }
}
