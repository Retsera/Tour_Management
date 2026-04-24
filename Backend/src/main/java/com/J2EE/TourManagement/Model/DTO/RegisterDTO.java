package com.J2EE.TourManagement.Model.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDTO {
  @NotBlank(message = "Tên người dùng không được để trống.")
  private String fullname;
  @NotBlank(message = "Email người dùng không được để trống.")
  private String email;

  @NotBlank(message = "Mật khẩu không được để trống.") private String password;

  private String confirmPassword;
}
