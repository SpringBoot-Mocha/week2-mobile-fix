package com.riwi.mobile_fix.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.riwi.mobile_fix.dto.CreateRepairOrderRequest;
import com.riwi.mobile_fix.dto.UpdateOrderStatusRequest;
import com.riwi.mobile_fix.exception.BusinessRuleException;
import com.riwi.mobile_fix.exception.ForbiddenException;
import com.riwi.mobile_fix.exception.ResourceNotFoundException;
import com.riwi.mobile_fix.model.DeviceModel;
import com.riwi.mobile_fix.model.RepairOrder;
import com.riwi.mobile_fix.model.RepairStatus;
import com.riwi.mobile_fix.model.Role;
import com.riwi.mobile_fix.model.UserModel;
import com.riwi.mobile_fix.repository.RepairOrderRepository;
import com.riwi.mobile_fix.service.DeviceService;
import com.riwi.mobile_fix.service.OrderService;
import com.riwi.mobile_fix.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final RepairOrderRepository orderRepository;
    private final UserService userService;
    private final DeviceService deviceService;

    @Override
    public RepairOrder createOrder(CreateRepairOrderRequest request, String username) {
        UserModel customer = userService.getUserByUsername(username);
        DeviceModel device = deviceService.getDeviceById(request.getDeviceId());

        RepairOrder order = new RepairOrder();
        order.setCustomer(customer);
        order.setDevice(device);
        order.setIssueDescription(request.getIssueDescription());
        order.setStatus(RepairStatus.PENDING);

        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepairOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RepairOrder> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepairOrder> getOrdersByStatus(RepairStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepairOrder> getOrdersByCustomer(String username) {
        UserModel customer = userService.getUserByUsername(username);
        return orderRepository.findByCustomer(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepairOrder> getOrdersByTechnician(String username) {
        UserModel technician = userService.getUserByUsername(username);
        return orderRepository.findByTechnician(technician);
    }

    @Override
    @Transactional(readOnly = true)
    public RepairOrder getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repair order not found with id: " + id));
    }

    @Override
    public RepairOrder assignTechnician(Long orderId, Long technicianId, String requestingUsername) {
        UserModel requestingUser = userService.getUserByUsername(requestingUsername);
        
        // Only ADMIN can assign technicians
        if (requestingUser.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Only administrators can assign technicians");
        }

        RepairOrder order = getOrderById(orderId);
        UserModel technician = userService.getUserById(technicianId);

        // Verify that the user is a technician
        if (technician.getRole() != Role.TECH) {
            throw new BusinessRuleException("Selected user is not a technician");
        }

        order.setTechnician(technician);
        return orderRepository.save(order);
    }

    @Override
    public RepairOrder updateStatus(Long orderId, UpdateOrderStatusRequest request, String username) {
        UserModel user = userService.getUserByUsername(username);
        RepairOrder order = getOrderById(orderId);

        // Validate user permissions
        if (user.getRole() == Role.USER) {
            throw new ForbiddenException("Users cannot update order status");
        }

        // TECH can only update their assigned orders
        if (user.getRole() == Role.TECH) {
            if (order.getTechnician() == null || !order.getTechnician().getId().equals(user.getId())) {
                throw new ForbiddenException("You can only update orders assigned to you");
            }
        }

        // Validate status transitions
        validateStatusTransition(order.getStatus(), request.getStatus());

        order.setStatus(request.getStatus());
        if (request.getTechNotes() != null && !request.getTechNotes().isEmpty()) {
            order.setTechNotes(request.getTechNotes());
        }

        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long orderId, String username) {
        UserModel user = userService.getUserByUsername(username);
        RepairOrder order = getOrderById(orderId);

        // ADMIN can delete any order
        if (user.getRole() == Role.ADMIN) {
            orderRepository.delete(order);
            return;
        }

        // USER can only delete their own orders and only if PENDING
        if (user.getRole() == Role.USER) {
            if (!order.getCustomer().getId().equals(user.getId())) {
                throw new ForbiddenException("You can only delete your own orders");
            }
            if (order.getStatus() != RepairStatus.PENDING) {
                throw new BusinessRuleException("You can only delete orders with PENDING status");
            }
            orderRepository.delete(order);
            return;
        }

        // TECH cannot delete orders
        throw new ForbiddenException("Technicians cannot delete orders");
    }

    private void validateStatusTransition(RepairStatus currentStatus, RepairStatus newStatus) {
        if (currentStatus == newStatus) {
            return; // Allow same status
        }

        switch (currentStatus) {
            case PENDING:
                if (newStatus != RepairStatus.IN_PROGRESS && newStatus != RepairStatus.CANCELLED) {
                    throw new BusinessRuleException(
                            "PENDING orders can only transition to IN_PROGRESS or CANCELLED");
                }
                break;
            case IN_PROGRESS:
                if (newStatus != RepairStatus.READY && newStatus != RepairStatus.CANCELLED) {
                    throw new BusinessRuleException(
                            "IN_PROGRESS orders can only transition to READY or CANCELLED");
                }
                break;
            case READY:
                if (newStatus != RepairStatus.DELIVERED && newStatus != RepairStatus.CANCELLED) {
                    throw new BusinessRuleException(
                            "READY orders can only transition to DELIVERED or CANCELLED");
                }
                break;
            case DELIVERED:
                throw new BusinessRuleException("DELIVERED orders cannot change status");
            case CANCELLED:
                throw new BusinessRuleException("CANCELLED orders cannot change status");
        }
    }
}
