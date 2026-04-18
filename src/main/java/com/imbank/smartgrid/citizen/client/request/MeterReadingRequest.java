package com.imbank.smartgrid.citizen.client.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeterReadingRequest {
    private String meterId;
    private String citizenId;
    private String providerName;
    private BigDecimal consumptionKwh;
    private String readingType;
    private LocalDateTime readingDate;
    private BigDecimal currentReading;

}
