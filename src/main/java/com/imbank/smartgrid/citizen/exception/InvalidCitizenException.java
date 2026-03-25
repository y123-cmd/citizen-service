package com.imbank.smartgrid.citizen.exception;

import lombok.Getter;

@Getter
public class InvalidCitizenException extends RuntimeException{
    private final String fieldName;
    private final Object rejectedValue;
    private final String reason;

    public InvalidCitizenException(String fieldName, Object rejectedValue, String reason){
        super(String.format("Invalid %s: '%s' - %s",  fieldName, rejectedValue,reason));
        this.fieldName = fieldName;
        this.rejectedValue = rejectedValue;
        this.reason = reason;
    }
}
