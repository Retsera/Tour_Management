package com.J2EE.TourManagement.Controller;

import com.J2EE.TourManagement.Model.DTO.BookingStatisticDTO;
import com.J2EE.TourManagement.Model.RestResponse;
import com.J2EE.TourManagement.Service.BookingStatisticService;
import com.J2EE.TourManagement.Util.annotation.ApiMessage;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/statistic")
@RequiredArgsConstructor
public class StatisticController {

  private final BookingStatisticService service;

  @GetMapping("/booking")
  @ApiMessage("Thống kê booking thành công")
  public ResponseEntity<BookingStatisticDTO>
  getBookingStatistic(@RequestParam("startDate") Instant startDate,
                      @RequestParam("endDate") Instant endDate) {
    return ResponseEntity.ok(service.getStatistics(startDate, endDate));
  }

  @GetMapping("/booking/last3days")
  public ResponseEntity<BookingStatisticDTO> getLast3Days() {
    return ResponseEntity.ok(service.getStatisticsLastNDays(3));
  }

  @GetMapping("/booking/last7days")
  public ResponseEntity<BookingStatisticDTO> getLast7Days() {
    return ResponseEntity.ok(service.getStatisticsLastNDays(7));
  }

  @GetMapping("/booking/last30days")
  public ResponseEntity<BookingStatisticDTO> getLast30Days() {
    return ResponseEntity.ok(service.getStatisticsLastNDays(30));
  }

  @GetMapping("/booking/last12months")
  public ResponseEntity<BookingStatisticDTO> getLast12Months() {
    return ResponseEntity.ok(service.getStatisticsLast12Months());
  }
}
