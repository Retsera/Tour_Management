package com.J2EE.TourManagement.Service;

import com.J2EE.TourManagement.Model.DTO.BookingStatisticDTO;
import com.J2EE.TourManagement.Repository.BookingRep;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingStatisticService {

  private final BookingRep bookingRepository;

  public BookingStatisticDTO getStatistics(Instant start, Instant end) {
    BookingStatisticDTO dto = new BookingStatisticDTO();

    dto.setTotalBooking(
        safeLong(bookingRepository.countBookingsCompleteBetween(start, end)));
    dto.setTotalTour(safeLong(bookingRepository.totalTourBetween(start, end)));
    dto.setTotalRevenue(
        safeDouble(bookingRepository.getTotalRevenueBetween(start, end)));
    dto.setTotalTourBooked(
        safeLong(bookingRepository.countDistinctTourBookedBetween(start, end)));

    return dto;
  }

  private long safeLong(Long value) { return value != null ? value : 0L; }

  private double safeDouble(Double value) {
    return value != null ? value : 0.0;
  }

  public BookingStatisticDTO getStatisticsLastNDays(long days) {
    Instant now = Instant.now();
    Instant start = now.minus(days, ChronoUnit.DAYS);
    return getStatistics(start, now);
  }

  public BookingStatisticDTO getStatisticsLast12Months() {
    Instant now = Instant.now();
    Instant start = now.minus(365, ChronoUnit.DAYS);
    return getStatistics(start, now);
  }
}
