package com.imbank.smartgrid.citizen.dto.response;

import com.imbank.smartgrid.citizen.entity.ProviderName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitizenResponse {

    private String citizenId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private ProviderName providerName;
    private LocalDateTime createdAt;
}