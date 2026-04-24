package com.J2EE.TourManagement.Service;

import com.J2EE.TourManagement.Model.UserVoucher;
import com.J2EE.TourManagement.Repository.UserVoucherRepo;
import com.J2EE.TourManagement.Repository.VoucherRepo;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserVoucherService {

  private final UserVoucherRepo userVoucherRepo;

  private final VoucherRepo voucherRepo;



  
}
