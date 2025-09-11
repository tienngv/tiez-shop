package com.tiezshop.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import com.tiezshop.constrain.ErrorConst;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TShopException extends RuntimeException {

    private final String errorCode;
    private final String message;
    private final String timestamp;
    private final transient Object data;

    public TShopException(ErrorConst err) {
        this.errorCode = err.getErrCode();
        this.message = err.getMessage();
        this.data = null;
        this.timestamp = String.valueOf(LocalDateTime.now());
    }

    public TShopException(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
        this.data = null;
        this.timestamp = String.valueOf(LocalDateTime.now());

    }

    public TShopException(String errorCode, String message, Object data) {
        this.errorCode = errorCode;
        this.message = message;
        this.data = data;
        this.timestamp = String.valueOf(LocalDateTime.now());

    }
}

