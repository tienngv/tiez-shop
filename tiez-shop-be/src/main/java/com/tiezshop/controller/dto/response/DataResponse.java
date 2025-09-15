package com.tiezshop.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataResponse<T> {
    @Builder.Default
    private String errorCode = "2000";
    @Builder.Default
    private String timestamp = LocalDateTime.now().toString();
    private String message;
    private T result;

}
