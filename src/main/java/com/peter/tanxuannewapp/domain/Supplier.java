package com.peter.tanxuannewapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.peter.tanxuannewapp.util.JwtTokenUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "suppliers")
@Getter
@Setter
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Name must not be blank!")
    private String name;

    @NotNull
    private boolean active;

    @NotNull
    private String contactInfo;

    @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Product> products;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdAt = Instant.now();
        this.createdBy = JwtTokenUtil.getCurrentUserLogin().orElse(null);
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedAt = Instant.now();
        this.updatedBy = JwtTokenUtil.getCurrentUserLogin().orElse(null);
    }
}
