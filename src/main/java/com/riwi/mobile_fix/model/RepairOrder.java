package com.riwi.mobile_fix.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "repair_orders")
@Getter
@Setter
@NoArgsConstructor
public class RepairOrder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private UserModel customer;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private DeviceModel device;

    @NotBlank(message = "Issue description is mandatory")
    @Size(min = 10, message = "Issue description must be at least 10 characters")
    @Column(nullable = false, length = 1000)
    private String issueDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RepairStatus status = RepairStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "technician_id")
    private UserModel technician;

    @Column(length = 2000)
    private String techNotes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public RepairOrder(UserModel customer, DeviceModel device, String issueDescription) {
        this.customer = customer;
        this.device = device;
        this.issueDescription = issueDescription;
        this.status = RepairStatus.PENDING;
    }

}
