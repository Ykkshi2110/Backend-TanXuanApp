package com.peter.tanxuannewapp.domain.resposne;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationResponse {
    private Meta meta;
    private Object result;
}
