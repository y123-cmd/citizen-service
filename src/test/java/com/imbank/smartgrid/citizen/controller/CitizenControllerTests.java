package com.imbank.smartgrid.citizen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imbank.smartgrid.citizen.dto.request.CitizenRequest;
import com.imbank.smartgrid.citizen.dto.response.CitizenResponse;
import com.imbank.smartgrid.citizen.entity.CitizenStatus;
import com.imbank.smartgrid.citizen.entity.MeterType;
import com.imbank.smartgrid.citizen.entity.ProviderName;
import com.imbank.smartgrid.citizen.exception.CitizenNotFoundException;
import com.imbank.smartgrid.citizen.service.CitizenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CitizenController.class)
@ActiveProfiles("test")
@WithMockUser
class CitizenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CitizenService citizenService;

    private CitizenResponse testResponse;
    private CitizenRequest testRequest;

    @BeforeEach
    void setUp() {
        testRequest = new CitizenRequest();
        testRequest.setName("John Doe");
        testRequest.setProviderName(ProviderName.KPLC);
        testRequest.setMeterType(MeterType.AUTOMATED);

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
    void registerCitizen_WhenValidRequest_Returns201() throws Exception {
        when(citizenService.registerCitizen(any(CitizenRequest.class)))
                .thenReturn(testResponse);

        mockMvc.perform(post("/api/v1/citizens")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.citizenId").value("CIT-KPLC-00001"));
    }

    @Test
    void registerCitizen_WhenInvalidRequest_Returns400() throws Exception {
        CitizenRequest invalidRequest = new CitizenRequest();
        invalidRequest.setName("");

        mockMvc.perform(post("/api/v1/citizens")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCitizenById_WhenExists_Returns200() throws Exception {
        when(citizenService.getCitizenById(1L)).thenReturn(testResponse);

        mockMvc.perform(get("/api/v1/citizens/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.citizenId").value("CIT-KPLC-00001"));
    }

    @Test
    void getCitizenById_WhenNotExists_Returns404() throws Exception {
        when(citizenService.getCitizenById(99L))
                .thenThrow(new CitizenNotFoundException("citizen", "id", 99L));

        mockMvc.perform(get("/api/v1/citizens/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getCitizenByCitizenId_WhenExists_Returns200() throws Exception {
        when(citizenService.getCitizenByCitizenId("CIT-KPLC-00001"))
                .thenReturn(testResponse);

        mockMvc.perform(get("/api/v1/citizens/citizen/CIT-KPLC-00001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.citizenId").value("CIT-KPLC-00001"));
    }

    @Test
    void getAllCitizens_Returns200() throws Exception {
        Page<CitizenResponse> page = new PageImpl<>(List.of(testResponse));
        when(citizenService.getAllCitizens(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/citizens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void getCitizensByProvider_Returns200() throws Exception {
        Page<CitizenResponse> page = new PageImpl<>(List.of(testResponse));
        when(citizenService.getCitizensByProvider(any(ProviderName.class), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/citizens/provider/KPLC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void getCitizensByMeterType_Returns200() throws Exception {
        Page<CitizenResponse> page = new PageImpl<>(List.of(testResponse));
        when(citizenService.getCitizensByMeterType(any(MeterType.class), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/citizens/metertype/AUTOMATED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void updateCitizensStatus_WhenValidStatus_Returns200() throws Exception {
        when(citizenService.updateCitizensStatus("CIT-KPLC-00001", "INACTIVE"))
                .thenReturn(testResponse);

        mockMvc.perform(patch("/api/v1/citizens/CIT-KPLC-00001/status")
                        .with(csrf())
                        .param("status", "INACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void submitMeterReading_Returns200() throws Exception {
        mockMvc.perform(post("/api/v1/citizens/CIT-KPLC-00001/readings")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }
}