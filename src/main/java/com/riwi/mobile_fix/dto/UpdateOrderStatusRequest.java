package com.riwi.mobile_fix.dto;

import com.riwi.mobile_fix.model.RepairStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {
    
    @NotNull(message = "Status is mandatory")
    private RepairStatus status;
    
    private String techNotes;
}
