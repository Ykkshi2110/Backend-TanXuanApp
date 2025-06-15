package com.peter.tanxuannewapp.domain;

import com.peter.tanxuannewapp.type.OrderStatusEnum;
import com.peter.tanxuannewapp.util.JwtTokenUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Order Code must not be blank!")
    @Column(columnDefinition = "TEXT")
    private String code;

    @NotBlank
    private String receiverName;

    @NotBlank
    private String receiverPhone;

    @NotBlank
    private String receiverAddress;

    private OrderStatusEnum status;

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
