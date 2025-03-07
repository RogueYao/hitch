package com.yian.commons.exception;

import com.yian.commons.enums.BusinessErrors;

public class BusinessRuntimeException extends RuntimeException {

    private BusinessErrors businessError;

    public BusinessRuntimeException(BusinessErrors businessError) {
        super(businessError.getMsg());
        this.businessError = businessError;
    }

    public BusinessRuntimeException(BusinessErrors businessError, String message) {
        super(message);
        this.businessError = businessError;
    }

    public BusinessErrors getBusinessError() {
        return businessError;
    }
}
