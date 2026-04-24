package com.J2EE.TourManagement.Model.DTO;

import com.J2EE.TourManagement.Model.Voucher.DiscountType;
import com.J2EE.TourManagement.Util.annotation.EndDateAfterStartDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EndDateAfterStartDate
public class VoucherDTO {

  @NotBlank(message = "Code không được để trống") private String code;

  @NotBlank(message = "Description không được để trống")
  private String description;

  @NotNull(message = "DiscountType không được để trống")
  private DiscountType discountType;

  @NotNull(message = "DiscountValue không được để trống")
  @Positive(message = "DiscountValue phải lớn hơn 0")
  private Double discountValue;

  @NotNull(message = "MinOrder không được để trống")
  @PositiveOrZero(message = "MinOrder >= 0")
  private Double minOrder;

  @PositiveOrZero(message = "MaxDiscount >= 0") private Double maxDiscount;

  @NotNull(message = "Quantity không được để trống")
  @Positive(message = "Quantity > 0")
  private Integer quantity;

  @NotNull(message = "Start date is required")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime startDate; // ngày bắt đầu

  @NotNull(message = "End date is required")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime endDate; // ngày kết thúc
}
