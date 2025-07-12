package com.peter.tanxuannewapp.domain.resposne;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCustomerDTO {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Instant createdAt;
    private Instant updatedAt;
    private ResRoleDTO role;
}
