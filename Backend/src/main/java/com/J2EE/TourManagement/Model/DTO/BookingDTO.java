package com.J2EE.TourManagement.Model.DTO;

import com.J2EE.TourManagement.Util.constan.EnumStatusBooking;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class BookingDTO {

  private long userId;
  private String note;
  private String orderCode;

  @Enumerated(EnumType.STRING) private EnumStatusBooking status;

  @NotBlank(message = "Email người dùng không được để trống.")
  private String contactEmail;

  @NotBlank(message = "Tên người dùng không được để trống.")
  private String contactFullname;

  @NotBlank(message = "Địa chỉ người dùng không được để trống.")
  private String contactAddress;

  @NotBlank(message = "Số điện thoại người dùng không được để trống.")
  private String contactPhone;

 private Long paymentId;

  // ✅ Sửa kiểu dữ liệu thành BookingDetailDTO
  private List<BookingDetailDTO> bookingDetails;

  // --- Getters & Setters ---
  public long getUserId() { return userId; }
  public void setUserId(long userId) { this.userId = userId; }

  public String getNote() { return note; }
  public void setNote(String note) { this.note = note; }

  public EnumStatusBooking getStatus() { return status; }
  public void setStatus(EnumStatusBooking status) { this.status = status; }

  public String getContactEmail() { return contactEmail; }
  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }

  public String getContactPhone() { return contactPhone; }
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

  public Long getPaymentId() { return paymentId; }
  public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

  public List<BookingDetailDTO> getBookingDetails() { return bookingDetails; }
  public void setBookingDetails(List<BookingDetailDTO> bookingDetails) {
    this.bookingDetails = bookingDetails;
  }
    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }
}
