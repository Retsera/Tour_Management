package com.J2EE.TourManagement.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.J2EE.TourManagement.Model.Payment;

import java.util.Optional;

@Repository
public interface PaymentRep extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBookingId(Long bookingId);
}
