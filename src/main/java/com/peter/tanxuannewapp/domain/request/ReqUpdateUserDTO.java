package com.peter.tanxuannewapp.domain.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateUserDTO {
    private int id;

    @NotBlank(message = "Name must not be blank!")
    private String name;

    @NotBlank(message = "Address must not be blank!")
    private String address;

    @NotBlank(message = "Email must not be blank!")
    @Email(message = "Invalid Email!", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String email;

    @NotBlank(message = "Phone must not be blank!")
    @Pattern(regexp = "^(0[35789])(\\d{8})$", message = "Invalid phone number!")
    private String phone;
}
