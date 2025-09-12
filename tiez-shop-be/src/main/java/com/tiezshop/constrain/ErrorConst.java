package com.tiezshop.constrain;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorConst {
    NOT_FOUND("4123","Not Found"),
    UNAUTHENTICATED("4124","Unauthenticated"),
    BAD_REQUEST("4125","Bad Request"),
    VALIDATION("4126","Invalid data input"),
    SUCCESS("222","Success"),
    UNKNOWN("999","Unknown error");

    private final String errCode;
    private final String message;

}
