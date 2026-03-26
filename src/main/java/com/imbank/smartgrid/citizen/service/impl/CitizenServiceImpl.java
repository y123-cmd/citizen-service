package com.imbank.smartgrid.citizen.service.impl;

import com.imbank.smartgrid.citizen.client.ProviderServiceClient;
import com.imbank.smartgrid.citizen.client.request.MeterReadingRequest;
import com.imbank.smartgrid.citizen.dto.request.CitizenRequest;
import com.imbank.smartgrid.citizen.dto.response.CitizenResponse;
import com.imbank.smartgrid.citizen.entity.*;
import com.imbank.smartgrid.citizen.exception.CitizenNotFoundException;
import com.imbank.smartgrid.citizen.exception.InvalidCitizenException;
import com.imbank.smartgrid.citizen.exception.ProviderServiceUnavailableException;
import com.imbank.smartgrid.citizen.mapper.CitizenMapper;
import com.imbank.smartgrid.citizen.repository.CitizenRepository;
import com.imbank.smartgrid.citizen.repository.MeterRepository;
import com.imbank.smartgrid.citizen.service.CitizenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CitizenServiceImpl implements CitizenService {

    private final CitizenRepository citizenRepository;
    private final CitizenMapper citizenMapper;
    private final MeterRepository meterRepository;
    private final ProviderServiceClient providerServiceClient;

    @Override
    public CitizenResponse registerCitizen(CitizenRequest request) {
        log.info("Registering new citizen for provider: {}", request.getProviderName());

        long count = citizenRepository.countByProviderName(request.getProviderName());
        String sequence = String.format("%05d", count + 1);
        String citizenId = String.format("CIT-%s-%s", request.getProviderName(), sequence);
        String meterPrefix = request.getMeterType() == MeterType.AUTOMATED ? "SM" : "MN";
        String meterId = String.format("%s-%s-%s", request.getProviderName(), meterPrefix, sequence);

        Citizen citizen = citizenMapper.toEntity(request, citizenId, meterId);
        Citizen savedCitizen = citizenRepository.save(citizen);

        Meter meter = new Meter();
        meter.setMeterId(meterId);
        meter.setCitizenId(citizenId);
        meter.setProviderName(request.getProviderName());
        meter.setMeterType(request.getMeterType());
        meterRepository.save(meter);


        log.info("Citizen registered successfully with citizenId: {}", citizenId);
        return citizenMapper.toResponse(savedCitizen);
    }
    @Override
    @Transactional(readOnly = true)
    public CitizenResponse getCitizenById(Long id){
        log.info("Fetching citizen with id: {}", id);
        Citizen citizen = citizenRepository.findById(id)
                .orElseThrow(() -> new CitizenNotFoundException("citizen", "id", id));
        return citizenMapper.toResponse(citizen);
    }
    @Override
    @Transactional(readOnly = true)
    public CitizenResponse getCitizenByCitizenId(String citizenId){
        log.info("Fetching citizen with citizenId: {}", citizenId);
        Citizen citizen = citizenRepository.findByCitizenId(citizenId)
                .orElseThrow(() -> new CitizenNotFoundException("citizen", "citizenId", citizenId));
                return citizenMapper.toResponse(citizen);
    }
    @Override
    @Transactional(readOnly = true)
    public  Page<CitizenResponse> getAllCitizens(Pageable pageable){
        log.info("Fetching all citizens - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return citizenRepository.findAll(pageable)
                .map(citizenMapper :: toResponse);

    }
    @Override
    @Transactional(readOnly = true)
    public Page<CitizenResponse>getCitizensByProvider(ProviderName providerName , Pageable pageable){
        log.info("Fetching citizens for provider: {}", providerName);
        return citizenRepository.findByProviderName(providerName,pageable)
                .map(citizenMapper :: toResponse);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<CitizenResponse>getCitizensByMeterType(MeterType meterType, Pageable pageable){
        log.info("Fetching citizens with meter type: {}", meterType);
        return citizenRepository.findByMeterType(meterType,pageable)
                .map(citizenMapper :: toResponse);
    }
    @Override
    public CitizenResponse updateCitizensStatus(String citizenId, String status){
        log.info("Updating status for citizenId: {} to {}", citizenId, status);
        Citizen citizen = citizenRepository.findByCitizenId(citizenId)
                .orElseThrow(() -> new CitizenNotFoundException("citizen","citizenId",citizenId));
        try {
            CitizenStatus newStatus = CitizenStatus.valueOf(status.toUpperCase());
            citizen.setStatus(newStatus);
        }catch (IllegalArgumentException e) {
            throw new InvalidCitizenException("status", status,"status must be ACTIVE or INACTIVE");
        }
        Citizen updatedCitizen = citizenRepository.save(citizen);
        log.info("Citizen status updated successfully for citizenId: {}", citizenId);
        return citizenMapper.toResponse(updatedCitizen);
    }
    @Override
    public void submitMeterReading(String citizenId){
        log.info("Submitting meter reading for citizenId: {}", citizenId);

        Citizen citizen = citizenRepository.findByCitizenId(citizenId)
                .orElseThrow(() -> new CitizenNotFoundException("citizen","citizenId",citizenId));
        if (citizen.getStatus() == CitizenStatus.INACTIVE){
            log.warn("Skipping reading for inactive citizen: {}", citizenId);
            return;
        }
        Meter meter = meterRepository.findByCitizenId(citizenId)
                .orElseThrow(() -> new CitizenNotFoundException("meter", "citizenId", citizenId));

        BigDecimal lastReading = meter.getLastReading();
        BigDecimal increment = BigDecimal.valueOf(10 + Math.random() * 90);
        BigDecimal newReading = lastReading.add(increment).setScale(2, RoundingMode.HALF_UP);
        BigDecimal consumptionKwh = newReading.subtract(lastReading).setScale(2, RoundingMode.HALF_UP);

        MeterReadingRequest request = new MeterReadingRequest();
        request.setMeterId(meter.getMeterId());
        request.setCitizenId(citizenId);
        request.setProviderName(citizen.getProviderName().name());
        request.setConsumptionKwh(consumptionKwh);
        request.setReadingType(citizen.getMeterType() == MeterType.AUTOMATED ? "AUTOMATED" : "MANUAL");
        request.setReadingDate(LocalDateTime.now());

        try {
            providerServiceClient.submitReading(request);
            log.info("Reading submitted successfully for citizenId: {}", citizenId);
        } catch (Exception e) {
            log.error("Failed to submit reading for citizenId: {}", citizenId, e);
            throw new ProviderServiceUnavailableException(
                    "Provider Service is unavailable. Please try again later.", e);
        }

        meter.setLastReading(newReading);
        meterRepository.save(meter);
        log.info("Meter last reading updated to: {} for citizenId: {}", newReading, citizenId);
    }
}
