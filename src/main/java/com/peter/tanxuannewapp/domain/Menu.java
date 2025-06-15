package com.peter.tanxuannewapp.domain;

import com.peter.tanxuannewapp.util.JwtTokenUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "menu")
@Getter
@Setter
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    private String name;

//    @OneToMany(mappedBy = "menu", fetch = FetchType.LAZY)
//    private List<Permission> permissions;

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
