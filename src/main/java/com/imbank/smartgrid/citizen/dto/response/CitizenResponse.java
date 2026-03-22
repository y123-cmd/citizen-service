package com.imbank.smartgrid.citizen.dto.response;


import com.imbank.smartgrid.citizen.entity.CitizenStatus;
import com.imbank.smartgrid.citizen.entity.MeterType;
import com.imbank.smartgrid.citizen.entity.ProviderName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitizenResponse {
    private  Long id;
    private String citizenId;
    private String name;
    private ProviderName providerName;
    private MeterType meterType;
    private CitizenStatus status;
    private String meterId;
    private LocalDateTime createdAt;
}
