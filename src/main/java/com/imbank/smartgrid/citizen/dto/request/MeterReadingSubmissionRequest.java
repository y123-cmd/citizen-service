package com.imbank.smartgrid.citizen.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeterReadingSubmissionRequest {

    @NotBlank(message = "Meter ID is required")
    @Size(max = 20, message = "Meter ID must not exceed 20 characters")
    private String meterId;

    @NotNull(message = "Consumption reading is required")
    @DecimalMin(value = "0.01", message = "Consumption must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Invalid consumption format")
    private BigDecimal consumptionKwh;
}