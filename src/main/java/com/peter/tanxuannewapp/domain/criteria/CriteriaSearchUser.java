package com.peter.tanxuannewapp.domain.criteria;

import com.peter.tanxuannewapp.domain.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CriteriaSearchUser {
    private String email;
    private String name;
    private String phone;
    private String address;
    private String createdAt;
    private Role role;
}
