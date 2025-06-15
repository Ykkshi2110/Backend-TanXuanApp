package com.peter.tanxuannewapp.domain;

import com.peter.tanxuannewapp.util.JwtTokenUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @NotBlank(message = "Password must not be blank!")
    @Size(min = 12, message = "Password must be at least 12 character!")
    private String password;

    private Instant updatedAt;
    private Instant createdAt;
    private String createdBy;
    private String updatedBy;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @PrePersist
    public void handleBeforeCreate(){
        this.createdAt = Instant.now();
        this.createdBy = JwtTokenUtil.getCurrentUserLogin().orElse(null);
    }

    @PreUpdate
    public void handleBeforeUpdate(){
        this.updatedAt = Instant.now();
        this.updatedBy = JwtTokenUtil.getCurrentUserLogin().orElse(null);
    }
}
