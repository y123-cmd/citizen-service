package com.imbank.smartgrid.citizen.dto.response;

import com.imbank.smartgrid.citizen.entity.ProviderName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeterStateResponse {

    private String meterId;
    private String citizenId;
    private ProviderName providerName;
    private BigDecimal lastReadingKwh;
    private LocalDateTime lastReadingDate;
    private Boolean isSmartMeter;
    private LocalDateTime updatedAt;
}