package com.riwi.mobile_fix.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.riwi.mobile_fix.dto.CreateRepairOrderRequest;
import com.riwi.mobile_fix.dto.UpdateOrderStatusRequest;
import com.riwi.mobile_fix.model.RepairOrder;
import com.riwi.mobile_fix.model.RepairStatus;

public interface OrderService {
    
    RepairOrder createOrder(CreateRepairOrderRequest request, String username);
    
    List<RepairOrder> getAllOrders();
    
    Page<RepairOrder> getAllOrders(Pageable pageable);
    
    List<RepairOrder> getOrdersByStatus(RepairStatus status);
    
    List<RepairOrder> getOrdersByCustomer(String username);
    
    List<RepairOrder> getOrdersByTechnician(String username);
    
    RepairOrder getOrderById(Long id);
    
    RepairOrder assignTechnician(Long orderId, Long technicianId, String requestingUsername);
    
    RepairOrder updateStatus(Long orderId, UpdateOrderStatusRequest request, String username);
    
    void deleteOrder(Long orderId, String username);
}
