package com.imbank.smartgrid.citizen.controller;

import com.imbank.smartgrid.citizen.dto.callback.CallbackRequest;
import com.imbank.smartgrid.citizen.dto.response.ApiResponse;
import com.imbank.smartgrid.citizen.service.CallbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/callbacks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Callbacks", description = "APIs for receiving callbacks from the Provider Service")
public class CallbackController {
    private final CallbackService callbackService;

    @Operation(summary = "Handle reading confirmation callback", description = "Receives a callback from the Provider Service confirming whether a meter reading was saved successfully or failed")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Callback processed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid callback secret"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Missing or invalid request body")
    })
    @PostMapping("reading-confirmation")
    public ResponseEntity<ApiResponse<Void>> handleReadingCallback(
            @RequestHeader("X-Callback-Secret") String secret,
            @Valid @RequestBody CallbackRequest request) {
        log.info("Received callback for citizenId: {} status: {}",
                request.getCitizenId(), request.getStatus());
        callbackService.processCallBack(secret, request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Callback processed successfully", null));
    }
}