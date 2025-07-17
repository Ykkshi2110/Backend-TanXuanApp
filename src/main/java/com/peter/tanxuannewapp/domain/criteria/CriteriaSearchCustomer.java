package com.peter.tanxuannewapp.domain.criteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CriteriaSearchCustomer {
    private String email;
    private String name;
    private String phone;
    private String address;
    private String createdAt;
}
