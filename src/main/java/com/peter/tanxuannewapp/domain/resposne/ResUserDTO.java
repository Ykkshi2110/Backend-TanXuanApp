package com.peter.tanxuannewapp.domain.resposne;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResUserDTO {
    private int id;
    private String name;
    private String address;
    private String email;
    private String phone;
    private Instant createdAt;
    private Instant updatedAt;
    private ResRoleDTO role;
}
