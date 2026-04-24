package com.J2EE.TourManagement.Repository;
import com.J2EE.TourManagement.Model.Booking;
import com.J2EE.TourManagement.Model.User;
import com.J2EE.TourManagement.Util.constan.EnumStatusBooking;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRep
    extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {
  List<Booking> findByUser(User user);
  Optional<Booking> findByUserIdAndStatus(Long userId,
                                          EnumStatusBooking status);

  @Query(
      "SELECT b FROM Booking b WHERE b.status = 'PENDING' AND (b.expiredAt < :now OR b.expiredAt IS NULL)")
  List<Booking>
  findPendingAndExpired(@Param("now") Instant now);

   @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = 'COMPLETED' AND b.createdAt >= :start AND b.createdAt <= :end")
    Long countBookingsCompleteBetween(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT COUNT(bd) FROM BookingDetail bd WHERE bd.booking.status = 'COMPLETED' AND bd.booking.createdAt >= :start AND bd.booking.createdAt <= :end")
    Long totalTourBetween(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.status = 'COMPLETED' AND b.createdAt >= :start AND b.createdAt <= :end")
    Double getTotalRevenueBetween(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT COUNT(DISTINCT bd.tourDetail.id) FROM BookingDetail bd WHERE bd.booking.status = 'COMPLETED' AND bd.booking.createdAt >= :start AND bd.booking.createdAt <= :end")
    Long countDistinctTourBookedBetween(@Param("start") Instant start, @Param("end") Instant end);
}
