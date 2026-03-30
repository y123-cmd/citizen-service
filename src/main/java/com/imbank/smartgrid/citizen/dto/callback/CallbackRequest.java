package com.imbank.smartgrid.citizen.dto.callback;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallbackRequest {
    @NotBlank(message = "citizen id is required")
    @Size(max = 20,message = "citizen id must not exceed 20 characters")
    private String citizenId;

    @NotBlank(message = "meter id is required")
    @Size(max = 20,message = "meter id must not exceed 20 characters")
    private String meterId;

    @NotBlank(message = "provider name is required")
    private String providerName;


    @NotBlank(message = "status is required")
    private String status;

    @NotBlank(message = "message is required")
    private String message;

}
