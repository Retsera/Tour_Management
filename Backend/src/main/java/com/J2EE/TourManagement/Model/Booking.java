package com.J2EE.TourManagement.Model;

import com.J2EE.TourManagement.Util.constan.EnumStatusBooking;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "bookings")

public class Booking {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_user", nullable = false)
  @JsonBackReference("user-booking")
  private User user;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "#,###.##")
  private double totalPrice;

  private String note;

  @Enumerated(EnumType.STRING) private EnumStatusBooking status;

  private String contactEmail;

  private String contactFullname;

  private String contactAddress;

  private String contactPhone;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:s a", timezone = "GMT+7")
  private Instant createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:s a", timezone = "GMT+7")
  private Instant updatedAt;

  @Column(name = "expiredAt", nullable = false)
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:s a", timezone = "GMT+7")
  private Instant expiredAt;

  @Column(name = "orderCode")
  private String orderCode;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Payment payment;

  @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL,
             orphanRemoval = true)
  @JsonManagedReference
  private List<BookingDetail> bookingDetails;

  public long getId() { return this.id; }

  public void setId(long id) { this.id = id; }

  public User getUser() { return this.user; }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public void setUser(User user) { this.user = user; }

  public Double getTotalPrice() { return this.totalPrice; }

  public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

  public String getNote() { return this.note; }

  public void setNote(String note) { this.note = note; }

  public EnumStatusBooking getStatus() { return this.status; }

  public void setStatus(EnumStatusBooking status) { this.status = status; }

  public String getContactEmail() { return this.contactEmail; }

  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }

  public String getContactPhone() { return this.contactPhone; }

  public void setContactPhone(String contactPhone) {
    this.contactPhone = contactPhone;
  }

  public String getContactFullname() { return this.contactFullname; }

  public void setContactFullname(String contactFullname) {
    this.contactFullname = contactFullname;
  }

  public String getContactAddress() { return this.contactAddress; }

  public void setContactAddress(String contactAddress) {
    this.contactAddress = contactAddress;
  }

  public Instant getCreatedAt() { return this.createdAt; }

  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

  public Instant getUpdatedAt() { return this.updatedAt; }

  public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

  public List<BookingDetail> getBookingDetails() { return this.bookingDetails; }

  public void setBookingDetails(List<BookingDetail> bookingDetails) {
    this.bookingDetails = bookingDetails;
  }

    public Instant getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Instant expiredAt) {
        this.expiredAt = expiredAt;
    }

  @PrePersist
  public void handleBeforeCreate() {
    this.createdAt = Instant.now();
  }

  @PreUpdate
  public void handleBeforeUpdate() {
    this.updatedAt = Instant.now();
  }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

}
