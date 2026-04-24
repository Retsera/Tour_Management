package com.J2EE.TourManagement.Model.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoucherRequestDTO {
  private Long userId;
  private String voucherCode;
}
