package com.peter.tanxuannewapp.domain.resposne;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL) // ignore field null value in response
public class ApiResponse<T> {
    private int statusCode;
    private String error;
    private Object message;
    private T data;
}
