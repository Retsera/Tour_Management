package com.J2EE.TourManagement.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "vouchers")
public class Voucher {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

  private String code;
  private String description;

  @Enumerated(EnumType.STRING) private DiscountType discountType;

  private Double discountValue;
  private Double minOrder;
  private Double maxDiscount;
  private Integer quantity;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime startDate; // ngày bắt đầu

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime endDate; // ngày kết thúc

  @Enumerated(EnumType.STRING) private VoucherStatus status;

  @OneToMany(mappedBy = "voucher")
  @JsonManagedReference("voucher-userVoucher")
  private List<UserVoucher> userVouchers;

  public enum DiscountType { PERCENT, AMOUNT }

  public enum VoucherStatus { ACTIVE, INACTIVE }
}
