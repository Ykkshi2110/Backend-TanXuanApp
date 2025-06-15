package com.peter.tanxuannewapp.domain.resposne;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResUpdateUserDTO {
    private int id;
    private String name;
    private String email;
    private String address;
    private String phone;
    private Instant updatedAt;
}
