package com.imbank.smartgrid.citizen.service;

import com.imbank.smartgrid.citizen.client.ProviderServiceClient;
import com.imbank.smartgrid.citizen.dto.request.CitizenRequest;
import com.imbank.smartgrid.citizen.dto.response.CitizenResponse;
import com.imbank.smartgrid.citizen.entity.*;
import com.imbank.smartgrid.citizen.exception.CitizenNotFoundException;
import com.imbank.smartgrid.citizen.exception.InvalidCitizenException;
import com.imbank.smartgrid.citizen.exception.ProviderServiceUnavailableException;
import com.imbank.smartgrid.citizen.mapper.CitizenMapper;
import com.imbank.smartgrid.citizen.repository.CitizenRepository;
import com.imbank.smartgrid.citizen.repository.MeterRepository;
import com.imbank.smartgrid.citizen.service.impl.CitizenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CitizenServiceImplTests {
    @Mock
    private CitizenRepository citizenRepository;
    @Mock
    private MeterRepository meterRepository;
    @Mock
    private CitizenMapper citizenMapper;
    @Mock
    private ProviderServiceClient providerServiceClient;

    @InjectMocks
    private CitizenServiceImpl citizenService;

    private Citizen testCitizen;
    private CitizenRequest testRequest;
    private CitizenResponse testResponse;


    @BeforeEach
    void setUp() {
        testRequest = new CitizenRequest();
        testRequest.setName("John Doe");
        testRequest.setProviderName(ProviderName.KPLC);
        testRequest.setMeterType(MeterType.AUTOMATED);

        testCitizen = new Citizen();
        testCitizen.setId(1L);
        testCitizen.setCitizenId("CIT-KPLC-00001");
        testCitizen.setName("John Doe");
        testCitizen.setProviderName(ProviderName.KPLC);
        testCitizen.setMeterType(MeterType.AUTOMATED);
        testCitizen.setMeterId("KPLC-SM-00001");
        testCitizen.setStatus(CitizenStatus.ACTIVE);

        testResponse = new CitizenResponse();
        testResponse.setId(1L);
        testResponse.setCitizenId("CIT-KPLC-00001");
        testResponse.setName("John Doe");
        testResponse.setProviderName(ProviderName.KPLC);
        testResponse.setMeterType(MeterType.AUTOMATED);
        testResponse.setMeterId("KPLC-SM-00001");
        testResponse.setStatus(CitizenStatus.ACTIVE);
    }
    @Test
    void registerCitizen_shouldReturnCitizenResponse_whenValidRequest() {
        when(citizenRepository.countByProviderName(ProviderName.KPLC)).thenReturn(0L);
        when(citizenMapper.toEntity(any(), anyString(), anyString())).thenReturn(testCitizen);
        when(citizenRepository.save(any(Citizen.class))).thenReturn(testCitizen);
        when(citizenMapper.toResponse(testCitizen)).thenReturn(testResponse);


        CitizenResponse result = citizenService.registerCitizen(testRequest);


        assertNotNull(result);
        assertEquals("CIT-KPLC-00001", result.getCitizenId());
        assertEquals("KPLC-SM-00001", result.getMeterId());
        assertEquals(CitizenStatus.ACTIVE, result.getStatus());
        verify(citizenRepository).save(any(Citizen.class));
    }
    @Test
    void getCitizenById_WhenExists_ReturnsCitizenResponse() {
        when(citizenRepository.findById(1L)).thenReturn(Optional.of(testCitizen));
        when(citizenMapper.toResponse(testCitizen)).thenReturn(testResponse);

        CitizenResponse result = citizenService.getCitizenById(1L);

        assertNotNull(result);
        assertEquals("CIT-KPLC-00001", result.getCitizenId());
        verify(citizenRepository).findById(1L);
    }

    @Test
    void getCitizenById_WhenNotExists_ThrowsCitizenNotFoundException() {
        when(citizenRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> citizenService.getCitizenById(99L))
                .isInstanceOf(CitizenNotFoundException.class)
                .hasMessageContaining("citizen not found with id");
    }
    @Test
    void updateCitizensStatus_WhenValidStatus_ReturnsUpdatedCitizen() {
        when(citizenRepository.findByCitizenId("CIT-KPLC-00001"))
                .thenReturn(Optional.of(testCitizen));
        when(citizenRepository.save(any(Citizen.class))).thenReturn(testCitizen);
        when(citizenMapper.toResponse(testCitizen)).thenReturn(testResponse);

        CitizenResponse result = citizenService.updateCitizensStatus("CIT-KPLC-00001", "ACTIVE");

        assertNotNull(result);
        verify(citizenRepository).save(any(Citizen.class));
    }

    @Test
    void updateCitizensStatus_WhenCitizenNotFound_ThrowsCitizenNotFoundException() {
        when(citizenRepository.findByCitizenId("CIT-KPLC-99999"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> citizenService.updateCitizensStatus("CIT-KPLC-99999", "ACTIVE"))
                .isInstanceOf(CitizenNotFoundException.class)
                .hasMessageContaining("citizen not found with citizenId");
    }

    @Test
    void updateCitizensStatus_WhenInvalidStatus_ThrowsInvalidCitizenException() {
        when(citizenRepository.findByCitizenId("CIT-KPLC-00001"))
                .thenReturn(Optional.of(testCitizen));

        assertThatThrownBy(() -> citizenService.updateCitizensStatus("CIT-KPLC-00001", "INVALID"))
                .isInstanceOf(InvalidCitizenException.class)
                .hasMessageContaining("Invalid status");
    }
    @Test
    void submitMeterReading_WhenCitizenIsInactive_SkipsReading() {
        testCitizen.setStatus(CitizenStatus.INACTIVE);
        when(citizenRepository.findByCitizenId("CIT-KPLC-00001"))
                .thenReturn(Optional.of(testCitizen));

        citizenService.submitMeterReading("CIT-KPLC-00001");

        verify(meterRepository, never()).findByCitizenId(any());
        verify(providerServiceClient, never()).submitReading(any());
    }
    @Test
    void submitMeterReading_WhenCitizenNotFound_ThrowsCitizenNotFoundException() {
        when(citizenRepository.findByCitizenId("CIT-KPLC-99999"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> citizenService.submitMeterReading("CIT-KPLC-99999"))
                .isInstanceOf(CitizenNotFoundException.class)
                .hasMessageContaining("citizen not found with citizenId");
    }
    @Test
    void submitMeterReading_WhenProviderServiceDown_ThrowsProviderServiceUnavailableException() {
        Meter testMeter = new Meter();
        testMeter.setMeterId("KPLC-SM-00001");
        testMeter.setCitizenId("CIT-KPLC-00001");
        testMeter.setProviderName(ProviderName.KPLC);
        testMeter.setMeterType(MeterType.AUTOMATED);
        testMeter.setLastReading(BigDecimal.ZERO);

        when(citizenRepository.findByCitizenId("CIT-KPLC-00001"))
                .thenReturn(Optional.of(testCitizen));
        when(meterRepository.findByCitizenId("CIT-KPLC-00001"))
                .thenReturn(Optional.of(testMeter));
        doThrow(new RuntimeException("Connection refused"))
                .when(providerServiceClient).submitReading(any());

        assertThatThrownBy(() -> citizenService.submitMeterReading("CIT-KPLC-00001"))
                .isInstanceOf(ProviderServiceUnavailableException.class)
                .hasMessageContaining("Provider Service is unavailable");
    }
    @Test
    void submitMeterReading_WhenSuccess_UpdatesMeterLastReading() {
        Meter testMeter = new Meter();
        testMeter.setMeterId("KPLC-SM-00001");
        testMeter.setCitizenId("CIT-KPLC-00001");
        testMeter.setProviderName(ProviderName.KPLC);
        testMeter.setMeterType(MeterType.AUTOMATED);
        testMeter.setLastReading(BigDecimal.ZERO);

        when(citizenRepository.findByCitizenId("CIT-KPLC-00001"))
                .thenReturn(Optional.of(testCitizen));
        when(meterRepository.findByCitizenId("CIT-KPLC-00001"))
                .thenReturn(Optional.of(testMeter));

        citizenService.submitMeterReading("CIT-KPLC-00001");

        verify(meterRepository).save(any(Meter.class));
        verify(providerServiceClient).submitReading(any());
    }
    @Test
    void getAllCitizens_ReturnsPageOfCitizens() {
        Page<Citizen> citizenPage = new PageImpl<>(List.of(testCitizen));
        when(citizenRepository.findAll(any(Pageable.class))).thenReturn(citizenPage);
        when(citizenMapper.toResponse(testCitizen)).thenReturn(testResponse);

        Page<CitizenResponse> result = citizenService.getAllCitizens(PageRequest.of(0, 10));

        assertNotNull(result);
        assertThat(result.getTotalElements()).isEqualTo(1L);
    }
    @Test
    void getCitizensByProvider_ReturnsPageOfCitizens() {
        Page<Citizen> citizenPage = new PageImpl<>(List.of(testCitizen));
        when(citizenRepository.findByProviderName(any(ProviderName.class), any(Pageable.class)))
                .thenReturn(citizenPage);
        when(citizenMapper.toResponse(testCitizen)).thenReturn(testResponse);

        Page<CitizenResponse> result = citizenService.getCitizensByProvider(
                ProviderName.KPLC, PageRequest.of(0, 10));

        assertNotNull(result);
        assertThat(result.getTotalElements()).isEqualTo(1L);
    }
    @Test
    void getCitizensByMeterType_ReturnsPageOfCitizens() {
        Page<Citizen> citizenPage = new PageImpl<>(List.of(testCitizen));
        when(citizenRepository.findByMeterType(any(MeterType.class), any(Pageable.class)))
                .thenReturn(citizenPage);
        when(citizenMapper.toResponse(testCitizen)).thenReturn(testResponse);

        Page<CitizenResponse> result = citizenService.getCitizensByMeterType(
                MeterType.AUTOMATED, PageRequest.of(0, 10));

        assertNotNull(result);
        assertThat(result.getTotalElements()).isEqualTo(1L);
    }
}
