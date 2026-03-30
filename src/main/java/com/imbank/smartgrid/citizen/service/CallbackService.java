package com.imbank.smartgrid.citizen.service;

import com.imbank.smartgrid.citizen.dto.callback.CallbackRequest;


public interface CallbackService{
    void processCallBack(String secret, CallbackRequest request);
}
