package com.riwi.mobile_fix.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "devices")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DeviceModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Device brand is mandatory")
    @Column(nullable = false)
    private String brand;

    @NotBlank(message = "Device model is mandatory")
    @Column(nullable = false)
    private String model;

    @Column(nullable = true)
    private String serialNumber;



}
