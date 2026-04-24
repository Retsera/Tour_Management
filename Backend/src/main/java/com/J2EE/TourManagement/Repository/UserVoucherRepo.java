package com.J2EE.TourManagement.Repository;

import com.J2EE.TourManagement.Model.UserVoucher;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserVoucherRepo extends JpaRepository<UserVoucher, Long> {
  List<UserVoucher> findAllByUserId(Long userId);
  boolean existsByUserIdAndVoucherId(Long userId, Long voucherId);
  Optional<UserVoucher>
  findByUser_IdAndVoucher_IdAndIsUsedFalse(Long userId, Long voucherId);
}