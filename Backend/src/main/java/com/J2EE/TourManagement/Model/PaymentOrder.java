package com.J2EE.TourManagement.Model;
import com.J2EE.TourManagement.Util.constan.EnumStatusPayment;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "payment_orders")
@Getter
@Setter
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderCode;

    private Long userId;

    private double amount;

    @Enumerated(EnumType.STRING)
    private EnumStatusPayment status; // PENDING, SUCCESS, FAILED, EXPIRED

    @Column(updatable = false, name = "createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+7")
    private LocalDateTime createdAt;

    @Column(updatable = false, name = "updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+7")
    private LocalDateTime updatedAt;

    @PrePersist
    public void create() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void update() {
        updatedAt = LocalDateTime.now();
    }


}
