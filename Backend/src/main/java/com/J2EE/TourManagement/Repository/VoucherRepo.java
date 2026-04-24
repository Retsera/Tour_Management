package com.J2EE.TourManagement.Repository;

import com.J2EE.TourManagement.Model.Booking;
import com.J2EE.TourManagement.Model.Voucher;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VoucherRepo
    extends JpaRepository<Voucher, Long>, JpaSpecificationExecutor<Voucher> {
  Optional<Voucher> findByCode(String code);
  boolean existsByCode(String code);
}