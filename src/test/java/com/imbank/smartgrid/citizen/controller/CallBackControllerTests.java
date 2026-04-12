package com.imbank.smartgrid.citizen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imbank.smartgrid.citizen.dto.callback.CallbackRequest;
import com.imbank.smartgrid.citizen.service.CallbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CallbackController.class)
@AutoConfigureMockMvc(addFilters = false)
class CallbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CallbackService callbackService;

    private CallbackRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new CallbackRequest();
        validRequest.setCitizenId("CIT-12345");
        validRequest.setMeterId("METER-98765");
        validRequest.setProviderName("Kenya Power");
        validRequest.setStatus("SUCCESS");
        validRequest.setMessage("Reading confirmed successfully");
    }

    @Test
    @DisplayName("Should successfully process valid reading confirmation callback")
    void shouldProcessValidReadingCallback() throws Exception {
        String secret = "my-callback-secret-123";

        doNothing().when(callbackService).processCallBack(eq(secret), any(CallbackRequest.class));

        mockMvc.perform(post("/api/v1/callbacks/reading-confirmation")
                        .header("X-Callback-Secret", secret)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Callback processed successfully"))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.pagination").isEmpty());
    }

    @Test
    @DisplayName("Should return 400 when X-Callback-Secret header is missing")
    void shouldReturnBadRequestWhenSecretHeaderIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/callbacks/reading-confirmation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Required request header 'X-Callback-Secret' is missing"));
    }

    @Test
    @DisplayName("Should return 400 when citizenId is blank")
    void shouldReturnBadRequestWhenCitizenIdIsBlank() throws Exception {
        CallbackRequest invalid = createRequestWithCitizenId("");

        mockMvc.perform(post("/api/v1/callbacks/reading-confirmation")
                        .header("X-Callback-Secret", "test-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when citizenId exceeds 20 characters")
    void shouldReturnBadRequestWhenCitizenIdIsTooLong() throws Exception {
        CallbackRequest invalid = createRequestWithCitizenId("CITIZEN-ID-THAT-IS-WAY-TOO-LONG-123456");

        mockMvc.perform(post("/api/v1/callbacks/reading-confirmation")
                        .header("X-Callback-Secret", "test-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when any required field is missing")
    void shouldReturnBadRequestWhenAnyRequiredFieldIsMissing() throws Exception {
        CallbackRequest invalid = new CallbackRequest();
        invalid.setCitizenId("CIT-12345");

        mockMvc.perform(post("/api/v1/callbacks/reading-confirmation")
                        .header("X-Callback-Secret", "test-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }


    private CallbackRequest createRequestWithCitizenId(String citizenId) {
        CallbackRequest req = new CallbackRequest();
        req.setCitizenId(citizenId);
        req.setMeterId("METER-98765");
        req.setProviderName("Kenya Power");
        req.setStatus("SUCCESS");
        req.setMessage("Reading confirmed successfully");
        return req;
    }
}