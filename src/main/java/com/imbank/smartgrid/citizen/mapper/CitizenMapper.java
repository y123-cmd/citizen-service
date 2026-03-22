package com.imbank.smartgrid.citizen.mapper;

import com.imbank.smartgrid.citizen.dto.request.CitizenRequest;
import com.imbank.smartgrid.citizen.dto.response.CitizenResponse;
import com.imbank.smartgrid.citizen.entity.Citizen;
import org.springframework.stereotype.Component;

@Component
public class CitizenMapper {
    public Citizen toEntity(CitizenRequest request, String citizenId, String meterId){
        Citizen citizen = new Citizen();
        citizen.setCitizenId(citizenId);
        citizen.setName(request.getName());
        citizen.setProviderName(request.getProviderName());
        citizen.setMeterType(request.getMeterType());
        citizen.setMeterId(meterId);
        return citizen;
    }
    public CitizenResponse toResponse(Citizen citizen){
        CitizenResponse response = new CitizenResponse();
            response.setId(citizen.getId());
            response.setCitizenId(citizen.getCitizenId());
            response.setName(citizen.getName());
            response.setProviderName(citizen.getProviderName());
            response.setMeterType(citizen.getMeterType());
            response.setStatus(citizen.getStatus());
            response.setMeterId(citizen.getMeterId());
            response.setCreatedAt(citizen.getCreatedAt());
            return response;
        }
    }

