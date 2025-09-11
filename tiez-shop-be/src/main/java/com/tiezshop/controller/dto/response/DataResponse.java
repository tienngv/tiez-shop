package com.tiezshop.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import com.tiezshop.constrain.ErrorConst;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DataResponse {
    private String errorCode;
    private String timestamp;
    private String message;
    private Object data;

    public DataResponse(String errorCode, String message) {
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = String.valueOf(LocalDateTime.now());
    }

    public DataResponse(ErrorConst ex, Map<String, String> data) {
        this.errorCode = ex.getErrCode();
        this.message = ex.getMessage();
        this.data = data;
        this.timestamp = String.valueOf(LocalDateTime.now());
    }

    public DataResponse(ErrorConst ex, Object data) {
        this.errorCode = ex.getErrCode();
        this.message = ex.getMessage();
        this.data = data;
        this.timestamp = String.valueOf(LocalDateTime.now());
    }

    public DataResponse(ErrorConst ex) {
        this.errorCode = ex.getErrCode();
        this.message = ex.getMessage();
        this.timestamp = String.valueOf(LocalDateTime.now());
    }

}
