package com.imbank.smartgrid.citizen.service;

import com.imbank.smartgrid.citizen.dto.request.CitizenRequest;
import com.imbank.smartgrid.citizen.dto.response.CitizenResponse;
import com.imbank.smartgrid.citizen.entity.MeterType;
import com.imbank.smartgrid.citizen.entity.ProviderName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CitizenService {
    CitizenResponse registerCitizen(CitizenRequest request);
    CitizenResponse getCitizenById (Long id);
    CitizenResponse getCitizenByCitizenId(String citizenId);
    Page<CitizenResponse> getAllCitizens(Pageable pageable);
    Page<CitizenResponse> getCitizensByProvider(ProviderName providerName,Pageable pageable);
    Page<CitizenResponse> getCitizensByMeterType(MeterType meterType, Pageable pageable);
    CitizenResponse updateCitizensStatus(String citizenId, String status);
    void submitMeterReading(String citizenId);
}
