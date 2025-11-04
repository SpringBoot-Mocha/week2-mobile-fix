package com.riwi.mobile_fix.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import com.riwi.mobile_fix.service.impl.OrderServiceImpl;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private RepairOrderRepository orderRepository;

    @Mock
    private UserService userService;

    @Mock
    private DeviceService deviceService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrder_ShouldCreatePendingOrder() {
        UserModel customer = buildUser(1L, Role.USER);
        DeviceModel device = buildDevice(10L);
        CreateRepairOrderRequest request = new CreateRepairOrderRequest(device.getId(), "Screen cracked badly");

        when(userService.getUserByUsername(customer.getUsername())).thenReturn(customer);
        when(deviceService.getDeviceById(device.getId())).thenReturn(device);
        when(orderRepository.save(any(RepairOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RepairOrder result = orderService.createOrder(request, customer.getUsername());

        assertNotNull(result);
        assertEquals(RepairStatus.PENDING, result.getStatus());
        assertEquals(customer, result.getCustomer());
        assertEquals(device, result.getDevice());

        ArgumentCaptor<RepairOrder> captor = ArgumentCaptor.forClass(RepairOrder.class);
        verify(orderRepository).save(captor.capture());
        assertEquals(RepairStatus.PENDING, captor.getValue().getStatus());
    }

    @Test
    void assignTechnician_AsAdmin_ShouldSucceed() {
        UserModel admin = buildUser(100L, Role.ADMIN);
        UserModel tech = buildUser(200L, Role.TECH);
        RepairOrder order = buildOrder(50L, admin);

        when(userService.getUserByUsername(admin.getUsername())).thenReturn(admin);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(userService.getUserById(tech.getId())).thenReturn(tech);
        when(orderRepository.save(order)).thenReturn(order);

        RepairOrder updated = orderService.assignTechnician(order.getId(), tech.getId(), admin.getUsername());

        assertEquals(tech, updated.getTechnician());
        verify(orderRepository).save(order);
    }

    @Test
    void assignTechnician_AsNonAdmin_ShouldThrowForbidden() {
        UserModel technician = buildUser(10L, Role.TECH);
        when(userService.getUserByUsername(technician.getUsername())).thenReturn(technician);

        assertThrows(ForbiddenException.class, () ->
                orderService.assignTechnician(1L, 2L, technician.getUsername()));

        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateStatus_ValidTransition_ShouldUpdate() {
        UserModel technician = buildUser(20L, Role.TECH);
        DeviceModel device = buildDevice(101L);
        RepairOrder order = buildOrder(77L, technician);
        order.setDevice(device);
        order.setTechnician(technician);
        order.setStatus(RepairStatus.PENDING);

        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(RepairStatus.IN_PROGRESS, "Disassembling device");

        when(userService.getUserByUsername(technician.getUsername())).thenReturn(technician);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        RepairOrder updated = orderService.updateStatus(order.getId(), request, technician.getUsername());

        assertEquals(RepairStatus.IN_PROGRESS, updated.getStatus());
        assertEquals("Disassembling device", updated.getTechNotes());
    }

    @Test
    void updateStatus_InvalidTransition_ShouldThrowBusinessRuleException() {
        UserModel technician = buildUser(20L, Role.TECH);
        RepairOrder order = buildOrder(77L, technician);
        order.setStatus(RepairStatus.PENDING);
        order.setTechnician(technician);

        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(RepairStatus.DELIVERED, null);

        when(userService.getUserByUsername(technician.getUsername())).thenReturn(technician);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        assertThrows(BusinessRuleException.class, () ->
                orderService.updateStatus(order.getId(), request, technician.getUsername()));
    }

    @Test
    void deleteOrder_UserOwnPending_ShouldDelete() {
        UserModel customer = buildUser(5L, Role.USER);
        RepairOrder order = buildOrder(9L, customer);
        order.setStatus(RepairStatus.PENDING);

        when(userService.getUserByUsername(customer.getUsername())).thenReturn(customer);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        orderService.deleteOrder(order.getId(), customer.getUsername());

        verify(orderRepository).delete(order);
    }

    @Test
    void deleteOrder_UserOtherOrder_ShouldThrowForbidden() {
        UserModel customer = buildUser(5L, Role.USER);
        UserModel other = buildUser(6L, Role.USER);
        RepairOrder order = buildOrder(9L, other);
        order.setStatus(RepairStatus.PENDING);

        when(userService.getUserByUsername(customer.getUsername())).thenReturn(customer);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        assertThrows(ForbiddenException.class, () ->
                orderService.deleteOrder(order.getId(), customer.getUsername()));

        verify(orderRepository, never()).delete(any());
    }

    @Test
    void deleteOrder_UserNotPending_ShouldThrowBusinessRule() {
        UserModel customer = buildUser(5L, Role.USER);
        RepairOrder order = buildOrder(9L, customer);
        order.setStatus(RepairStatus.IN_PROGRESS);

        when(userService.getUserByUsername(customer.getUsername())).thenReturn(customer);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        assertThrows(BusinessRuleException.class, () ->
                orderService.deleteOrder(order.getId(), customer.getUsername()));
    }

    @Test
    void getOrderById_NotFound_ShouldThrow() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(999L));
    }

    private UserModel buildUser(Long id, Role role) {
        UserModel user = new UserModel();
        user.setId(id);
        user.setUsername("user" + id);
        user.setRole(role);
        user.setPasswordHash("encoded");
        user.setFullName("User " + id);
        user.setEmail("user" + id + "@mail.com");
        user.setActive(true);
        return user;
    }

    private DeviceModel buildDevice(Long id) {
        DeviceModel device = new DeviceModel();
        device.setId(id);
        device.setBrand("Brand" + id);
        device.setModel("Model" + id);
        device.setSerialNumber("SN" + id);
        return device;
    }

    private RepairOrder buildOrder(Long id, UserModel customer) {
        RepairOrder order = new RepairOrder();
        order.setId(id);
        order.setCustomer(customer);
        return order;
    }
}
