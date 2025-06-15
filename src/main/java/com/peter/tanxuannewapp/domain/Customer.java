package com.peter.tanxuannewapp.domain;

import com.peter.tanxuannewapp.util.JwtTokenUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "customers")
@Getter
@Setter
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Name must not be blank!")
    private String name;

    @NotNull
    private String phone;

    @NotNull
    private String address;

    private double latitude;

    private double longitude;

    private int provinceId;
    private int districtId;
    private int wardId;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdAt = Instant.now();
        this.createdBy = JwtTokenUtil
                .getCurrentUserLogin()
                .orElse(null);
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedAt = Instant.now();
        this.updatedBy = JwtTokenUtil
                .getCurrentUserLogin()
                .orElse(null);
    }

}
