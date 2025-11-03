package com.riwi.mobile_fix.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRepairOrderRequest {
    
    @NotNull(message = "Device ID is mandatory")
    private Long deviceId;
    
    @NotBlank(message = "Issue description is mandatory")
    @Size(min = 10, message = "Issue description must be at least 10 characters")
    private String issueDescription;
}
