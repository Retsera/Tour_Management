package com.J2EE.TourManagement.Controller;

import com.J2EE.TourManagement.Model.Booking;
import com.J2EE.TourManagement.Model.User;
import com.J2EE.TourManagement.Model.DTO.BookingDTO;
import com.J2EE.TourManagement.Model.DTO.BookingResponseDTO;
import com.J2EE.TourManagement.Model.DTO.ResultPaginationDTO;
import com.J2EE.TourManagement.Service.BookingSer;
import com.J2EE.TourManagement.Service.PdfService;
import com.J2EE.TourManagement.Service.UserSer;
import com.J2EE.TourManagement.Util.annotation.ApiMessage;
import com.J2EE.TourManagement.Util.error.InvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.io.File;
import java.io.FileInputStream;
import java.io.InvalidClassException;
import java.io.OutputStream;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class BookingController {
  private final BookingSer bookingSer;
  private final UserSer userSer;
  private final PdfService pdfService;
  public BookingController(BookingSer bookingSer, UserSer userSer, PdfService pdfService) {
    this.bookingSer = bookingSer;
    this.userSer = userSer;
    this.pdfService = pdfService;
  }

  @PostMapping("/booking/create")
  @ApiMessage("Đặt tour thành công.")
  public ResponseEntity<?>
  postBooking(@RequestBody @Valid BookingDTO newBooking) throws InvalidException {
    BookingResponseDTO booking = this.bookingSer.handleSaveBooking(newBooking);

    return ResponseEntity.ok().body(booking);
  }

  @GetMapping("/booking/{id}")
  public ResponseEntity<?> getBookingById(@PathVariable("id") long id)
      throws InvalidException {

    Boolean isIdExist = this.bookingSer.isIdExist(id);
    if (!isIdExist) {
      throw new InvalidException("booking không tồn tại");
    }
    BookingResponseDTO booking = this.bookingSer.getBookingById(id);

    return ResponseEntity.ok().body(booking);
  }

  @GetMapping("/booking")
  public ResponseEntity<?> getAllBooking(@Filter Specification<Booking> spec,
                                         Pageable pageable) {

    return ResponseEntity.ok().body(
        this.bookingSer.getAllBooking(spec, pageable));
  }

  @PutMapping("booking/{id}")
  public ResponseEntity<?> putMethodName(@PathVariable("id") long id,
                                         @RequestBody BookingDTO bookingDTO)
      throws InvalidException {

    Boolean isIdExist = this.bookingSer.isIdExist(id);
    if (!isIdExist) {
      throw new InvalidException("booking không tồn tại");
    }

    BookingResponseDTO bookingResponseDTO =
        this.bookingSer.updateBooking(id, bookingDTO);
    return ResponseEntity.ok().body(bookingResponseDTO);
  }

  @GetMapping("/booking/user/{id}")
  public ResponseEntity<?> getBookingByUser(@PathVariable("id") long id) {
    User user = this.userSer.getUserById(id);
    List<Booking> listBookings = this.bookingSer.getBookingByUser(user);
    return ResponseEntity.ok().body(listBookings);
  }

  @GetMapping("/booking/{id}/download")
    public void downloadBookingPdf(@PathVariable("id") Long bookingId, HttpServletResponse response) {
        try {
            Booking booking = bookingSer.getById(bookingId); // lấy booking từ DB
            File pdfFile = pdfService.generateBookingPDF(booking);

            response.setContentType("application/pdf");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + pdfFile.getName());

            try (FileInputStream fis = new FileInputStream(pdfFile);
                 OutputStream os = response.getOutputStream()) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            }

        } catch (Exception e) {
            response.setStatus(500);
        }
    }

    // 2️⃣ Trả về PDF dưới dạng byte[] (frontend có thể hiển thị trực tiếp)
    @GetMapping("/booking/{id}/view")
    public void viewBookingPdf(@PathVariable("id") Long bookingId, HttpServletResponse response) {
        try {
            Booking booking = bookingSer.getById(bookingId); // lấy booking từ DB
            byte[] pdfBytes = pdfService.generateBookingPDFBytes(booking);

            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=Booking_" + booking.getId() + ".pdf");

            OutputStream os = response.getOutputStream();
            os.write(pdfBytes);
            os.flush();

        } catch (Exception e) {
            response.setStatus(500);
        }
    }
  
}
