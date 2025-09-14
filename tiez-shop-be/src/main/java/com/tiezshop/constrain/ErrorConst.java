package com.tiezshop.constrain;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorConst {
    NOT_FOUND("4123","Not Found"),
    UNAUTHORIZED("4124","UNAUTHORIZED"),
    BAD_REQUEST("4125","Bad Request"),
    CONFLICT("4126","Conflict Error"),
    INVALID_INPUT("4126","Invalid data input"),
    JSON_PARSE_ERROR("4127","JSON Parse Error"),
    SUCCESS("222","Success"),
    UNKNOWN("999","Unknown error");

    private final String errCode;
    private final String message;

}
