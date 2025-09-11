package com.tiezshop.constrain;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorConst {
    NOT_FOUND("404","Not Found"),
    BAD_REQUEST("400","Bad Request"),
    SUCCESS("200","Success"),
    VALIDATION("400","Invalid data input"),
    UNKNOWN("500","Unknown error");

    private final String errCode;
    private final String message;

}
