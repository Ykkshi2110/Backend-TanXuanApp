package com.peter.tanxuannewapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.peter.tanxuannewapp.util.JwtTokenUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "permissions")
@Getter
@Setter
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Permission name must not be blank!")
    private String name;

    @NotBlank(message = "Route must not be blank!")
    private String route;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "permissions")
    @JsonIgnore
    private List<Role> roles;

//    @ManyToOne
//    @JoinColumn(name = "menu_id")
//    private Menu menu;

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
