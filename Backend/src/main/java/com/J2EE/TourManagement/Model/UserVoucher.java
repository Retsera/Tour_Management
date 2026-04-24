package com.J2EE.TourManagement.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_vouchers")
@Getter
@Setter
public class UserVoucher {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  @JsonBackReference("user-userVoucher")
  private User user;

  @ManyToOne
  @JoinColumn(name = "voucher_id")
  @JsonBackReference("voucher-userVoucher")
  private Voucher voucher;

  private Boolean isUsed = false;
  private Instant usedDate;

  private String uniqueCode;
}
