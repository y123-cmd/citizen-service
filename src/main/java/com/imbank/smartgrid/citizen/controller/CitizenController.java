package com.imbank.smartgrid.citizen.controller;

import com.imbank.smartgrid.citizen.dto.request.CitizenRequest;
import com.imbank.smartgrid.citizen.dto.response.ApiResponse;
import com.imbank.smartgrid.citizen.dto.response.CitizenResponse;
import com.imbank.smartgrid.citizen.dto.response.Pagination;
import com.imbank.smartgrid.citizen.entity.MeterType;
import com.imbank.smartgrid.citizen.entity.ProviderName;
import com.imbank.smartgrid.citizen.service.CitizenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Citizens", description = "APIs for managing citizens and meter readings")
public class CitizenController {
    private final CitizenService citizenService;

    @Operation(summary = "Register a new citizen", description = "Registers a new citizen and assigns a meter based on provider and meter type")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Citizen registered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<CitizenResponse>> registerCitizen(
            @Valid @RequestBody CitizenRequest request) {
        log.info("REST request to register citizen for provider: {}", request.getProviderName());
        CitizenResponse response = citizenService.registerCitizen(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, "Citizen registered successfully", response));
    }

    @Operation(summary = "Get citizen by ID", description = "Retrieves a citizen by their database ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Citizen retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Citizen not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CitizenResponse>> getCitizenById(
            @Parameter(description = "Database ID of the citizen") @PathVariable Long id) {
        log.info("REST request to get citizen by id: {}", id);
        CitizenResponse response = citizenService.getCitizenById(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "Citizen Retrieved Successfully", response));
    }

    @Operation(summary = "Get citizen by citizen ID", description = "Retrieves a citizen by their unique citizen ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Citizen retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Citizen not found")
    })
    @GetMapping("/citizen/{citizenId}")
    public ResponseEntity<ApiResponse<CitizenResponse>> getCitizenByCitizenId(
            @Parameter(description = "Unique citizen ID e.g. CIT-KPLC-00001") @PathVariable String citizenId) {
        log.info("REST request to get citizen by citizenId: {}", citizenId);
        CitizenResponse response = citizenService.getCitizenByCitizenId(citizenId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Citizen Retrieved Successfully", response));
    }

    @Operation(summary = "Get all citizens", description = "Retrieves all citizens with pagination")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Citizens retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<CitizenResponse>>> getAllCitizens(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get all citizens - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<CitizenResponse> citizenPage = citizenService.getAllCitizens(pageable);
        Pagination pagination = new Pagination(
                citizenPage.getNumber() + 1,
                citizenPage.getSize(),
                citizenPage.getTotalElements(),
                citizenPage.getTotalPages()
        );
        return ResponseEntity.ok(new ApiResponse<>(200, "citizens retrieved successfully", citizenPage.getContent(), pagination));
    }

    @Operation(summary = "Get citizens by provider", description = "Retrieves all citizens for a specific electricity provider")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Citizens retrieved successfully")
    })
    @GetMapping("/provider/{providerName}")
    public ResponseEntity<ApiResponse<List<CitizenResponse>>> getCitizensByProvider(
            @Parameter(description = "Provider name: KPLC, TANESCO or UMEME") @PathVariable ProviderName providerName,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get citizens by provider: {}", providerName);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<CitizenResponse> citizenPage = citizenService.getCitizensByProvider(providerName, pageable);
        Pagination pagination = new Pagination(
                citizenPage.getNumber() + 1,
                citizenPage.getSize(),
                citizenPage.getTotalElements(),
                citizenPage.getTotalPages()
        );
        return ResponseEntity.ok(new ApiResponse<>(200, "citizens retrieved successfully", citizenPage.getContent(), pagination));
    }

    @Operation(summary = "Get citizens by meter type", description = "Retrieves all citizens filtered by meter type")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Citizens retrieved successfully")
    })
    @GetMapping("/metertype/{meterType}")
    public ResponseEntity<ApiResponse<List<CitizenResponse>>> getCitizensByMeterType(
            @Parameter(description = "Meter type: MANUAL or AUTOMATED") @PathVariable MeterType meterType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get citizens by meterType: {}", meterType);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<CitizenResponse> citizenPage = citizenService.getCitizensByMeterType(meterType, pageable);
        Pagination pagination = new Pagination(
                citizenPage.getNumber() + 1,
                citizenPage.getSize(),
                citizenPage.getTotalElements(),
                citizenPage.getTotalPages()
        );
        return ResponseEntity.ok(new ApiResponse<>(200, "citizens retrieved successfully", citizenPage.getContent(), pagination));
    }

    @Operation(summary = "Update citizen status", description = "Updates the status of a citizen to ACTIVE or INACTIVE")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Citizen status updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid status value"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Citizen not found")
    })
    @PatchMapping("/{citizenId}/status")
    public ResponseEntity<ApiResponse<CitizenResponse>> updateCitizensStatus(
            @Parameter(description = "Unique citizen ID") @PathVariable String citizenId,
            @RequestParam String status) {
        log.info("REST request to update status for citizenId: {}", citizenId);
        CitizenResponse response = citizenService.updateCitizensStatus(citizenId, status);
        return ResponseEntity.ok(new ApiResponse<>(200, "citizens updated successfully", response));
    }

    @Operation(summary = "Submit meter reading", description = "Manually submits a meter reading for a specific citizen")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Meter reading submitted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Citizen not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "503", description = "Provider service unavailable")
    })
    @PostMapping("/{citizenId}/readings")
    public ResponseEntity<ApiResponse<Void>> submitMeterReading(
            @Parameter(description = "Unique citizen ID") @PathVariable String citizenId) {
        log.info("REST request to submit meter reading for citizenId: {}", citizenId);
        citizenService.submitMeterReading(citizenId);
        return ResponseEntity.ok(new ApiResponse<>(200, "meter readings submitted successfully", null));
    }
}