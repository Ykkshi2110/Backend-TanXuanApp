package com.peter.tanxuannewapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.peter.tanxuannewapp.util.JwtTokenUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "Role name must not be null!")
    private String name;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<User> users;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"roles"})
    @JoinTable(name = "permission_role", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private List<Permission> permissions;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

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
