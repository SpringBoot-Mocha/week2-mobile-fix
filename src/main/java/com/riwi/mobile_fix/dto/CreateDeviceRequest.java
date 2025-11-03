package com.riwi.mobile_fix.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeviceRequest {
    
    @NotBlank(message = "Brand is mandatory")
    private String brand;
    
    @NotBlank(message = "Model is mandatory")
    private String model;
    
    private String serialNumber;
}
