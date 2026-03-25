package com.imbank.smartgrid.citizen.exception;

import lombok.Getter;

@Getter
public class CitizenNotFoundException extends RuntimeException{
    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    public CitizenNotFoundException(String resourceName, String fieldName,Object fieldValue){
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
        this.fieldName= fieldName;
        this.resourceName = resourceName;
        this.fieldValue = fieldValue;
    }
}

