package com.imbank.smartgrid.citizen.service.impl;

import com.imbank.smartgrid.citizen.dto.callback.CallbackRequest;
import com.imbank.smartgrid.citizen.exception.InvalidCallbackSecretException;
import com.imbank.smartgrid.citizen.service.CallbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j

public class CallbackServiceImpl implements CallbackService {
    @Value("${callback.secret}")
    private String expectedSecret;

@Override
    public void processCallBack(String secret, CallbackRequest request) {
    if (!secret.equals(expectedSecret)) {
        throw new InvalidCallbackSecretException("Invalid callback secret");
    }
    if ("SUCCESS".equals(request.getStatus())) {
        log.info("Reading confirmed for citizenId: {}, meterId:{}", request.getCitizenId(), request.getMeterId());
    } else {
        log.warn("Reading failed for citizenId: {}, meterId: {}, reason: {}", request.getCitizenId(), request.getMeterId(), request.getMessage());
    }

}


}
