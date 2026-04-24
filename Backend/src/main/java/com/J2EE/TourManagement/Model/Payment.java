package com.J2EE.TourManagement.Model;

import com.J2EE.TourManagement.Util.constan.EnumStatusPayment;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "payment")
public class Payment {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

  @OneToOne
  @JoinColumn(name = "id_booking")
  @JsonBackReference
  private Booking booking;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "#,###.##")
  private double amount;
  private String method;

  @Enumerated(EnumType.STRING) private EnumStatusPayment status;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:s a", timezone = "GMT+7")
  private Instant createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:s a", timezone = "GMT+7")
  private Instant updatedAt;

  public Long getId() { return this.id; }

  public void setId(Long id) { this.id = id; }

  public Booking getBooking() { return this.booking; }

  public void setBooking(Booking booking) { this.booking = booking; }

  public double getAmount() { return this.amount; }

  public void setAmount(double amount) { this.amount = amount; }

  public String getMethod() { return this.method; }

  public void setMethod(String method) { this.method = method; }

  public EnumStatusPayment getStatus() { return this.status; }

  public void setStatus(EnumStatusPayment status) { this.status = status; }

  public Instant getCreatedAt() { return this.createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

  public Instant getUpdatedAt() { return this.updatedAt; }

  public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

  @PrePersist
  public void handleBeforeCreate() {
    this.createdAt = Instant.now();
  }

  @PreUpdate
  public void handleBeforeUpdate() {
    this.updatedAt = Instant.now();
  }
}
