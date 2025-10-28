package com.mobilefix.mobilefix.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepairOrderModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String customer;
    private String device;
    private String issueDescription;
    private String status;
    private String assignedTech;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String techNotes;
}

