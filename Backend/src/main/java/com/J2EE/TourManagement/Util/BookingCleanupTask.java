package com.J2EE.TourManagement.Util;

import com.J2EE.TourManagement.Model.Booking;
import com.J2EE.TourManagement.Repository.BookingRep;
import com.J2EE.TourManagement.Service.BookingSer;
import com.J2EE.TourManagement.Service.PaymentOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class BookingCleanupTask {

    @Autowired
    private BookingRep bookingRepository;
    @Autowired
    private PaymentOrderService orderService;

    private BookingSer bookingService;

    @Scheduled(fixedRate = 120000)
    public void cleanupExpiredBookings() {
        Instant now = Instant.now();
        List<Booking> expiredBookings = bookingRepository.findPendingAndExpired(now);

        for (Booking booking : expiredBookings) {
            bookingService.cancelBookingAndRollbackStock(booking.getId());
            System.out.println("Đã hủy đơn treo: " + booking.getOrderCode());
        }
    }
}
