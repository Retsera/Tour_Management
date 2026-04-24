package com.J2EE.TourManagement.Model.DTO;

import com.J2EE.TourManagement.Model.Booking;
import com.J2EE.TourManagement.Model.BookingDetail;
import com.J2EE.TourManagement.Util.constan.EnumStatusBooking;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.util.List;

public class BookingResponseDTO {
  private long id;
  private long userId;
  private double totalPrice;
  private String note;
  private String orderCode;
  private EnumStatusBooking status;
  private String contactEmail;
  private String contactPhone;
  private String contactFullname;
  private String contactAddress;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:s a", timezone = "GMT+7")
  private Instant createdAt;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:s a", timezone = "GMT+7")
  private Instant updatedAt;
  private List<BookingDetail> bookingDetails;

  public BookingResponseDTO(Booking booking) {
    this.id = booking.getId();
    this.userId = booking.getUser().getId();
    this.totalPrice = booking.getTotalPrice();
    this.note = booking.getNote();
    this.orderCode = booking.getOrderCode();
    this.status = booking.getStatus();
    this.contactEmail = booking.getContactEmail();
    this.contactPhone = booking.getContactPhone();
    this.contactAddress = booking.getContactAddress();
    this.contactFullname = booking.getContactFullname();
    this.createdAt = booking.getCreatedAt();
    this.updatedAt = booking.getUpdatedAt();
    this.bookingDetails = booking.getBookingDetails();
  }

  public long getId() { return this.id; }

  public void setId(long id) { this.id = id; }

  public long getUserId() { return this.userId; }

  public void setUserId(long userId) { this.userId = userId; }

  public double getTotalPrice() { return this.totalPrice; }

  public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

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

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }
}
