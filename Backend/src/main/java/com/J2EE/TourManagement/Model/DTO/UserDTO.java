package com.J2EE.TourManagement.Model.DTO;

import com.J2EE.TourManagement.Model.Booking;
import com.J2EE.TourManagement.Model.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;

public class UserDTO {
  private long id;

  @NotBlank(message = "Tên người dùng không được để trống.")
  private String fullname;

  @NotBlank(message = "Email người dùng không được để trống.")
  private String email;

  private boolean status;

    private Role id_role;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:s a", timezone = "GMT+7")
  private Instant createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:s a", timezone = "GMT+7")
  private Instant updatedAt;

  private List<Booking> bookings;

  public UserDTO() {}

  public UserDTO(long id, String fullname, String email, boolean status, Role id_role,
                 Instant createdAt, Instant updatedAt) {
    this.id = id;
    this.fullname = fullname;
    this.email = email;
    this.status = status;
    this.id_role = id_role;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public long getId() { return this.id; }

  public void setId(long id) { this.id = id; }

  public String getFullName() { return this.fullname; }

  public void setFullName(String fullname) { this.fullname = fullname; }

  public String getEmail() { return this.email; }

  public void setEmail(String email) { this.email = email; }

  public boolean isStatus() { return this.status; }

  public boolean getStatus() { return this.status; }

  public void setStatus(boolean status) { this.status = status; }

  public Instant getCreatedAt() { return this.createdAt; }

  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

  public Instant getUpdatedAt() { return this.updatedAt; }

  public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

  public List<Booking> getBookings() { return this.bookings; }

  public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    public Role getRole() {
        return id_role;
    }

    public void setRole(Role id_role) {
        this.id_role = id_role;
    }
}
