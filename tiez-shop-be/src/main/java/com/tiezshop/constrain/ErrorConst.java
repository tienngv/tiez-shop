package com.tiezshop.constrain;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorConst {
    NOT_FOUND("4123", "The requested resource could not be found"),
    UNAUTHORIZED("4124", "Authentication is required or has failed"),
    BAD_REQUEST("4125", "The request is invalid or cannot be processed"),
    INVALID_INPUT("4126", "The provided input data is invalid or incomplete"),
    JSON_PARSE_ERROR("4127", "The request body contains malformed or invalid JSON"),
    FORBIDDEN("4128", "You do not have permission to access this resource"),
    CONFLICT("4129", "The request could not be completed due to a conflict with the current state of the resource"),
    SUCCESS("222", "The request has been processed successfully"),
    UNKNOWN("999", "An unknown error has occurred");

    private final String errCode;
    private final String message;

}
