package com.imbank.smartgrid.citizen.controller;

import com.imbank.smartgrid.citizen.dto.request.CitizenRequest;
import com.imbank.smartgrid.citizen.dto.response.ApiResponse;
import com.imbank.smartgrid.citizen.dto.response.CitizenResponse;
import com.imbank.smartgrid.citizen.dto.response.Pagination;
import com.imbank.smartgrid.citizen.entity.MeterType;
import com.imbank.smartgrid.citizen.entity.ProviderName;
import com.imbank.smartgrid.citizen.service.CitizenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/citizens")
@RequiredArgsConstructor
@Slf4j
public class CitizenController {
    private final CitizenService citizenService;

    @PostMapping
    public ResponseEntity<ApiResponse<CitizenResponse>> registerCitizen(
            @Valid @RequestBody CitizenRequest request) {
        log.info("REST request to register citizen for provider: {}", request.getProviderName());
        CitizenResponse response = citizenService.registerCitizen(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, "Citizen registered successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CitizenResponse>> getCitizenById(@PathVariable Long id) {
        log.info("REST request to get citizen by id: {}", id);
        CitizenResponse response = citizenService.getCitizenById(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "Citizen Retrieved Successfully", response));
    }

    @GetMapping("/citizen/{citizenId}")
    public ResponseEntity<ApiResponse<CitizenResponse>> getCitizenByCitizenId(@PathVariable String citizenId){
        log.info("REST request to get citizen by citizenId: {}", citizenId);
        CitizenResponse response = citizenService.getCitizenByCitizenId(citizenId);
        return ResponseEntity.ok(new ApiResponse<>(200,"Citizen Retrieved Successfully", response));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<CitizenResponse>>> getAllCitizens(
            @RequestParam(defaultValue = "1")int page,
            @RequestParam(defaultValue = "10")int size){
        log.info("REST request to get all citizens - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page -1, size);
        Page<CitizenResponse> citizenPage = citizenService.getAllCitizens(pageable);
        Pagination pagination = new Pagination(
                citizenPage.getNumber() + 1,
                citizenPage.getSize(),
                citizenPage.getTotalElements(),
                citizenPage.getTotalPages()
        );
        return ResponseEntity.ok(new ApiResponse<>(200,"citizens retrieved successfully",citizenPage.getContent(),pagination));
    }
    @GetMapping("/provider/{providerName}")
    public ResponseEntity<ApiResponse<List<CitizenResponse>>> getCitizensByProvider(
            @PathVariable ProviderName providerName,
            @RequestParam(defaultValue = "1")int page,
            @RequestParam(defaultValue = "10")int size){
        log.info("REST request to get citizens by provider: {}", providerName);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<CitizenResponse>citizenPage = citizenService.getCitizensByProvider(providerName, pageable);
        Pagination pagination = new Pagination(
                citizenPage.getNumber() +1,
                citizenPage.getSize(),
                citizenPage.getTotalElements(),
                citizenPage.getTotalPages()
        );
        return ResponseEntity.ok(new ApiResponse<>(200,"citizens retrieved successfully",citizenPage.getContent(),pagination));
    }
    @GetMapping("/metertype/{meterType}")
    public ResponseEntity<ApiResponse<List<CitizenResponse>>> getCitizensByMeterType(
            @PathVariable MeterType meterType,
            @RequestParam(defaultValue = "1")int page,
            @RequestParam(defaultValue = "10")int size){
        log.info("REST request to get citizens by meterType: {}", meterType);
        Pageable pageable = PageRequest.of(page -1, size);
        Page<CitizenResponse>citizenPage = citizenService.getCitizensByMeterType(meterType,pageable);
        Pagination pagination = new Pagination(
                citizenPage.getNumber() +1 ,
                citizenPage.getSize(),
                citizenPage.getTotalElements(),
                citizenPage.getTotalPages()
        );
        return ResponseEntity.ok(new ApiResponse<>(200,"citizens retrieved successfully",citizenPage.getContent(),pagination));

    }
    @PatchMapping("/{citizenId}/status")
    public ResponseEntity<ApiResponse<CitizenResponse>> updateCitizensStatus(
            @PathVariable String citizenId,
            @RequestParam String status) {
        log.info("REST request to update status for citizenId: {}", citizenId);
        CitizenResponse response = citizenService.updateCitizensStatus(citizenId,status);
        return ResponseEntity.ok(new ApiResponse<>(200,"citizens updated successfully", response));
    }
    @PostMapping("/{citizenId}/readings")
    public ResponseEntity<ApiResponse<Void>>submitMeterReading(
            @PathVariable String citizenId) {
        log.info("REST request to submit meter reading for citizenId: {}", citizenId);
        citizenService.submitMeterReading(citizenId);
        return ResponseEntity.ok(new ApiResponse<>(200,"meter readings submitted successfully",null));
    }
    }

