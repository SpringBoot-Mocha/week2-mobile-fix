package com.riwi.mobile_fix.controller.view;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.riwi.mobile_fix.model.DeviceModel;
import com.riwi.mobile_fix.model.RepairOrder;
import com.riwi.mobile_fix.model.RepairStatus;
import com.riwi.mobile_fix.model.Role;
import com.riwi.mobile_fix.model.UserModel;
import com.riwi.mobile_fix.service.DeviceService;
import com.riwi.mobile_fix.service.OrderService;
import com.riwi.mobile_fix.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final OrderService orderService;
    private final DeviceService deviceService;
    private final UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        UserModel currentUser = userService.getUserByUsername(username);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("repairStatuses", RepairStatus.values());

        switch (currentUser.getRole()) {
            case USER -> populateUserDashboard(model, currentUser);
            case TECH -> populateTechnicianDashboard(model, currentUser);
            case ADMIN -> populateAdminDashboard(model);
        }

        return "dashboard";
    }

    private void populateUserDashboard(Model model, UserModel user) {
        List<RepairOrder> myOrders = orderService.getOrdersByCustomer(user.getUsername());
        List<DeviceModel> devices = deviceService.getAllDevices();

        model.addAttribute("orders", myOrders);
        model.addAttribute("devices", devices);
    }

    private void populateTechnicianDashboard(Model model, UserModel technician) {
        List<RepairOrder> assignedOrders = orderService.getOrdersByTechnician(technician.getUsername());
        model.addAttribute("orders", assignedOrders);
    }

    private void populateAdminDashboard(Model model) {
        List<RepairOrder> allOrders = orderService.getAllOrders();
        List<DeviceModel> devices = deviceService.getAllDevices();
        List<UserModel> technicians = userService.getUsersByRole(Role.TECH);
        List<UserModel> users = userService.getAllUsers();

        model.addAttribute("orders", allOrders);
        model.addAttribute("devices", devices);
        model.addAttribute("users", users);
        model.addAttribute("technicians", technicians);
    }
}
