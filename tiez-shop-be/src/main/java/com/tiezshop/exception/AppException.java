package com.tiezshop.exception;

import com.fasterxml.jackson.annotation.JsonInclude;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
//@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppException extends RuntimeException {
    private final String errorCode;
    private final String message;
    private final String timestamp;
    private final transient Object data;

    public AppException(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
        this.data = null;
        this.timestamp = String.valueOf(LocalDateTime.now());

    }

}

