package com.tiezshop.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.tiezshop.constrain.ErrorConst;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
//@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TiezShopException extends RuntimeException {

    private final String errorCode;
    private final String message;
    private final String timestamp;
    private final transient Object data;

    public TiezShopException(ErrorConst err) {
        this.errorCode = err.getErrCode();
        this.message = err.getMessage();
        this.data = null;
        this.timestamp = String.valueOf(LocalDateTime.now());
    }

    public TiezShopException(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
        this.data = null;
        this.timestamp = String.valueOf(LocalDateTime.now());

    }

    public TiezShopException(String errorCode, String message, Object data) {
        this.errorCode = errorCode;
        this.message = message;
        this.data = data;
        this.timestamp = String.valueOf(LocalDateTime.now());

    }
}

