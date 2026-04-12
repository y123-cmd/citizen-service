package com.imbank.smartgrid.citizen.service;

import com.imbank.smartgrid.citizen.dto.callback.CallbackRequest;
import com.imbank.smartgrid.citizen.exception.InvalidCallbackSecretException;
import com.imbank.smartgrid.citizen.service.impl.CallbackServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class CallBackServiceTests {
        @InjectMocks
        private CallbackServiceImpl callbackService;

        private CallbackRequest successRequest;
        private CallbackRequest failedRequest;

        @BeforeEach
        void setUp() {
            ReflectionTestUtils.setField(callbackService,
                    "expectedSecret", "test-callback-secret");

            successRequest = new CallbackRequest();
            successRequest.setCitizenId("CIT-KPLC-00001");
            successRequest.setMeterId("KPLC-SM-00001");
            successRequest.setProviderName("KPLC");
            successRequest.setStatus("SUCCESS");
            successRequest.setMessage("Reading saved successfully");

            failedRequest = new CallbackRequest();
            failedRequest.setCitizenId("CIT-KPLC-00001");
            failedRequest.setMeterId("KPLC-SM-00001");
            failedRequest.setProviderName("KPLC");
            failedRequest.setStatus("FAILED");
            failedRequest.setMessage("Reading failed");
        }

        @Test
        void processCallback_WhenCorrectSecret_ProcessesSuccessfully() {
            assertThatNoException()
                    .isThrownBy(() -> callbackService
                            .processCallBack("test-callback-secret", successRequest));
        }

        @Test
        void processCallback_WhenWrongSecret_ThrowsInvalidCallbackSecretException() {
            assertThatThrownBy(() -> callbackService
                    .processCallBack("wrongsecret", successRequest))
                    .isInstanceOf(InvalidCallbackSecretException.class)
                    .hasMessageContaining("Invalid callback secret");
        }

        @Test
        void processCallback_WhenStatusFailed_ProcessesWithoutException() {
            assertThatNoException()
                    .isThrownBy(() -> callbackService
                            .processCallBack("test-callback-secret", failedRequest));
        }

        @Test
        void processCallback_WhenStatusSuccess_ProcessesWithoutException() {
            assertThatNoException()
                    .isThrownBy(() -> callbackService
                            .processCallBack("test-callback-secret", successRequest));
        }
    }

