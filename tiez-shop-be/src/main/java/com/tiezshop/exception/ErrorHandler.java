package com.tiezshop.exception;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.tiezshop.constrain.ErrorConst;
import com.tiezshop.controller.dto.response.DataResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

@Log4j2
@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        log.error("[Error-ValidException] : [{}]", ex.getLocalizedMessage());
        var error = new LinkedHashMap<String, String>();
        for (var err : ex.getFieldErrors()) {
            var key = err.getField();
            var value = err.getDefaultMessage();
            error.put(key, value);
        }
        DataResponse<Object> response = DataResponse.builder()
                .errorCode(ErrorConst.BAD_REQUEST.getErrCode()).result(error).build();
        return new ResponseEntity<>(response, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        log.error("[EXCEPTION 500] : [{} , time {}]", ex.toString(), LocalDateTime.now(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(DataResponse.builder().errorCode(ErrorConst.UNKNOWN.getErrCode()).message(ex.getLocalizedMessage()).build());
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<Object> handleJsonProcessingException(JsonProcessingException ex) {
        log.error("[EXCEPTION JSON format error] : [{} , time {}]", ex.getOriginalMessage(), LocalDateTime.now(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(DataResponse.builder().errorCode(ErrorConst.JSON_PARSE_ERROR.getErrCode()).message(ex.getLocalizedMessage()).build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("[EXCEPTION IllegalArgumentException] : {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(DataResponse.builder().errorCode(ErrorConst.INVALID_INPUT.getErrCode()).message(ex.getLocalizedMessage()).build());
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<Object> handleGpayException(AppException ex) {
        log.error("[EXCEPTION AppException] : {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                DataResponse.builder()
                        .errorCode(ex.getErrorCode())
                        .message(ex.getMessage())
                        .result(ex.getData()).build());
    }

}
