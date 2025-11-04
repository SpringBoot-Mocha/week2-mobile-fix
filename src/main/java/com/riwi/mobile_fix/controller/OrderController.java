package com.riwi.mobile_fix.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.riwi.mobile_fix.dto.CreateRepairOrderRequest;
import com.riwi.mobile_fix.dto.UpdateOrderStatusRequest;
import com.riwi.mobile_fix.model.RepairOrder;
import com.riwi.mobile_fix.model.RepairStatus;
import com.riwi.mobile_fix.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<?> getAllOrders(
            @RequestParam(required = false) RepairStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Principal principal) {
        
        if (status != null) {
            List<RepairOrder> orders = orderService.getOrdersByStatus(status);
            return ResponseEntity.ok(orders);
        }
        
        Page<RepairOrder> orders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<RepairOrder>> getMyOrders(Principal principal) {
        String username = principal.getName();
        List<RepairOrder> orders = orderService.getOrdersByCustomer(username);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/assigned")
    @PreAuthorize("hasRole('TECH')")
    public ResponseEntity<List<RepairOrder>> getAssignedOrders(Principal principal) {
        String username = principal.getName();
        List<RepairOrder> orders = orderService.getOrdersByTechnician(username);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepairOrder> getOrderById(@PathVariable Long id) {
        RepairOrder order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<RepairOrder> createOrder(
            @Valid @RequestBody CreateRepairOrderRequest request,
            Principal principal) {
        String username = principal.getName();
        RepairOrder order = orderService.createOrder(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @PutMapping("/{id}/assign/{techId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RepairOrder> assignTechnician(
            @PathVariable Long id,
            @PathVariable Long techId,
            Principal principal) {
        String username = principal.getName();
        RepairOrder order = orderService.assignTechnician(id, techId, username);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('TECH', 'ADMIN')")
    public ResponseEntity<RepairOrder> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            Principal principal) {
        String username = principal.getName();
        RepairOrder order = orderService.updateStatus(id, request, username);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteOrder(
            @PathVariable Long id,
            Principal principal) {
        String username = principal.getName();
        orderService.deleteOrder(id, username);
        return ResponseEntity.ok(Map.of("message", "Order deleted successfully"));
    }
}
