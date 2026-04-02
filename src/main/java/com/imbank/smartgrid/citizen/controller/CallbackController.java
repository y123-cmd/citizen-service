package com.imbank.smartgrid.citizen.controller;

import com.imbank.smartgrid.citizen.dto.callback.CallbackRequest;
import com.imbank.smartgrid.citizen.dto.response.ApiResponse;
import com.imbank.smartgrid.citizen.service.CallbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/callbacks")
@RequiredArgsConstructor
@Slf4j
public class CallbackController {
    private final CallbackService callbackService;

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
