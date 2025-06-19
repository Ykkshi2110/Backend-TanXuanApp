package com.peter.tanxuannewapp.domain.resposne;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResSupplierDTO {
    private int id;
    private String name;
    private String contactInfo;
    private boolean active;
    private Instant createdAt;
    private int totalProducts;
}
