package com.J2EE.TourManagement.Repository;

import com.J2EE.TourManagement.Model.PaymentOrder;
import com.J2EE.TourManagement.Util.constan.EnumStatusPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {


    Optional<PaymentOrder> findByOrderCode(String orderCode);


    List<PaymentOrder> findAllByStatusAndCreatedAtBefore(EnumStatusPayment status, LocalDateTime time);


    @Modifying
    @Query("UPDATE PaymentOrder p SET p.status = :status, p.updatedAt = :now WHERE p.id = :id")
    void updateStatus(@Param("id") Long id,
                      @Param("status") EnumStatusPayment status,
                      @Param("now") LocalDateTime now);
}
