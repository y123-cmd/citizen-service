package com.imbank.smartgrid.citizen.client;

import com.imbank.smartgrid.citizen.client.request.MeterReadingRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "electricity-provider-service", url = "${provider.service.url:http://localhost:8081}")
public interface ProviderServiceClient {
    @PostMapping("/api/v1/meter-readings")
    void submitReading(@RequestBody MeterReadingRequest request);
}
