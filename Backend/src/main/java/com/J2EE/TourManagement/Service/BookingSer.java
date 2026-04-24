package com.J2EE.TourManagement.Service;

import com.J2EE.TourManagement.Model.Booking;
import com.J2EE.TourManagement.Model.BookingDetail;
import com.J2EE.TourManagement.Model.DTO.BookingDTO;
import com.J2EE.TourManagement.Model.DTO.BookingDetailDTO;
import com.J2EE.TourManagement.Model.DTO.BookingResponseDTO;
import com.J2EE.TourManagement.Model.DTO.Meta;
import com.J2EE.TourManagement.Model.DTO.ResultPaginationDTO;
import com.J2EE.TourManagement.Model.TourDetail;
import com.J2EE.TourManagement.Model.User;
import com.J2EE.TourManagement.Repository.BookingDetailRep;
import com.J2EE.TourManagement.Repository.BookingRep;
import com.J2EE.TourManagement.Repository.TourDetailRepository;
import com.J2EE.TourManagement.Util.constan.EnumStatusBooking;
import com.J2EE.TourManagement.Util.constan.EnumTourDetailStatus;
import com.J2EE.TourManagement.Util.error.InvalidException;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingSer {

  private final BookingRep bookingRep;
  private final BookingDetailRep bookingDetailRep;
  private final TourDetailRepository tourDetailRepository;
  private final PaymentSer paymentSer;
  private final UserSer userSer;
  private final TourDetailService tourDetailSer;
  private final TourPriceService tourPriceSer;
  private final SimpMessagingTemplate messagingTemplate;
  private final EmailService emailService;

  //  public BookingSer(BookingRep bookingRep, BookingDetailRep
  //  bookingDetailRep, TourDetailRepository tourRepository,
  //  TourDetailRepository tourDetailRepository,
  //                    PaymentSer paymentSer, UserSer userSer,
  //                    TourDetailService tourDetailSer,
  //                    TourPriceService tourPriceSer, SimpMessagingTemplate
  //                    messagingTemplate) {
  //    this.bookingRep = bookingRep;
  //    this.bookingDetailRep = bookingDetailRep;
  //      this.tourDetailRepository = tourDetailRepository;
  //      this.paymentSer = paymentSer;
  //    this.userSer = userSer;
  //    this.tourDetailSer = tourDetailSer;
  //    this.tourPriceSer = tourPriceSer;
  //      this.messagingTemplate = messagingTemplate;
  //  }

  // ===================== CREATE =====================
  @Transactional
  public BookingResponseDTO handleSaveBooking(BookingDTO bookingDTO) throws InvalidException {
    Booking booking = new Booking();

    booking.setUser(userSer.getUserById(bookingDTO.getUserId()));
    booking.setNote(bookingDTO.getNote());
    //    booking.setStatus(bookingDTO.getStatus());
    booking.setContactEmail(bookingDTO.getContactEmail());
    booking.setContactPhone(bookingDTO.getContactPhone());
    booking.setOrderCode(bookingDTO.getOrderCode());
    booking.setContactFullname(bookingDTO.getContactFullname());
    booking.setContactAddress(bookingDTO.getContactAddress());
    booking.setStatus(EnumStatusBooking.PENDING);

    double totalPrice = 0;
    Integer capacity = 0;
    List<BookingDetail> details = new ArrayList<>();
    for (BookingDetailDTO dto : bookingDTO.getBookingDetails()) {
      BookingDetail bookingDetail = new BookingDetail();
      bookingDetail.setBooking(booking);
      bookingDetail.setQuantity(dto.getQuantity());
      bookingDetail.setStatus(true);

      bookingDetail.setTourDetail(
          tourDetailSer.getTourDetailById(dto.getTourDetailId()));
      bookingDetail.setTourPrice(
          tourPriceSer.getTourPriceById(dto.getTourPriceId()));

      double unitPrice = tourPriceSer.getTourPriceById(dto.getTourPriceId())
                             .getPrice()
                             .doubleValue();
      double price = dto.getQuantity() * unitPrice;

      bookingDetail.setPrice(price);

      totalPrice += price;
      capacity += dto.getQuantity();

      details.add(bookingDetail);
    }

    TourDetail tourDetail = tourDetailSer.getTourDetailById(
        bookingDTO.getBookingDetails().get(0).getTourDetailId());

    if (tourDetail.getRemainingSeats() < capacity) {
      throw new InvalidException("Hết chỗ!");
    }

    int newRemainingSeats = tourDetail.getRemainingSeats() - capacity;
    if (newRemainingSeats == 0) {
      tourDetail.setStatus(EnumTourDetailStatus.FULL);
    }

    tourDetail.setRemainingSeats(tourDetail.getRemainingSeats() - capacity);
    tourDetailRepository.save(tourDetail);
    String destination = "/topic/tour-seats/" + tourDetail.getId();
    messagingTemplate.convertAndSend(destination, newRemainingSeats);
    booking.setExpiredAt(Instant.now().plus(15, ChronoUnit.MINUTES));

    booking.setTotalPrice(totalPrice);
    booking.setBookingDetails(details);

    Booking savedBooking = bookingRep.save(booking);

    // Gửi email thông báo thành công
    String userEmail = booking.getContactEmail(); // giả sử booking có user
    String subject = "Vui lòng thanh toán - Đơn hàng ";
    String body = "Xin chào " + booking.getUser().getFullname() + ",\n\n"
                  + "Bạn vui lòng thanh toán đơn hàng #" + booking.getId() +
                  ".\n"
                  + "Booking ID: " + booking.getId() + "\n\n"
                  + "Booking sẽ huỷ sau 15p thanh toán không thành công" + "\n\n"
                  + "Cảm ơn bạn đã sử dụng dịch vụ!";

    emailService.sendPaymentSuccessEmail(userEmail, subject, body);

    return new BookingResponseDTO(savedBooking);
  }

  @Transactional
  public void cancelBookingAndRollbackStock(Long bookingId) {
    Booking booking = bookingRep.findById(bookingId).orElse(null);

    // Chỉ rollback nếu đơn đang là PENDING (để tránh rollback đơn đã fail trước
    // đó)
    if (booking != null && "PENDING".equals(booking.getStatus())) {
      TourDetail tourDetail = tourDetailSer.getTourDetailById(
          booking.getBookingDetails().get(0).getTourDetail().getId());
      tourDetail.setRemainingSeats(tourDetail.getRemainingSeats() +
                                   booking.getBookingDetails()
                                       .stream()
                                       .mapToInt(BookingDetail::getQuantity)
                                       .sum());
      tourDetailRepository.save(tourDetail);

      booking.setStatus(EnumStatusBooking.CANCELLED);
      bookingRep.save(booking);
    }
  }

  // ===================== UPDATE =====================
  public BookingResponseDTO updateBooking(Long bookingId, BookingDTO bookingDTO)
      throws InvalidException {
    Booking booking = bookingRep.findById(bookingId).orElseThrow(
        () -> new InvalidException("Booking không tồn tại"));

    if (booking.getStatus() != EnumStatusBooking.PENDING) {
      throw new InvalidException(
          "Không thể chỉnh sửa booking khi trạng thái không phải là PENDING");
    }

    booking.setNote(bookingDTO.getNote());
    booking.setStatus(bookingDTO.getStatus());
    booking.setContactEmail(bookingDTO.getContactEmail());
    booking.setContactPhone(bookingDTO.getContactPhone());
    booking.setContactFullname(bookingDTO.getContactFullname());
    booking.setContactAddress(bookingDTO.getContactAddress());

    booking.getBookingDetails().clear();

    double totalPrice = 0;
    List<BookingDetail> newDetails = new ArrayList<>();

    for (BookingDetailDTO dto : bookingDTO.getBookingDetails()) {
      BookingDetail detail = new BookingDetail();
      detail.setBooking(booking);
      detail.setQuantity(dto.getQuantity());

      detail.setPrice(dto.getQuantity() * dto.getPrice());
      detail.setStatus(true);

      detail.setTourDetail(
          tourDetailSer.getTourDetailById(dto.getTourDetailId()));
      detail.setTourPrice(tourPriceSer.getTourPriceById(dto.getTourPriceId()));

      double unitPrice = tourPriceSer.getTourPriceById(dto.getTourPriceId())
                             .getPrice()
                             .doubleValue();
      double price = dto.getQuantity() * unitPrice;

      detail.setPrice(price);

      totalPrice += detail.getPrice();
      newDetails.add(detail);
    }

    booking.setTotalPrice(totalPrice);
    booking.getBookingDetails().addAll(newDetails);

    Booking saved = bookingRep.save(booking);
    return new BookingResponseDTO(saved);
  }

  // ===================== READ =====================
  public BookingResponseDTO getBookingById(long id) {
    Booking booking = bookingRep.findById(id).orElseThrow(
        () -> new RuntimeException("Booking không tồn tại"));
    return new BookingResponseDTO(booking);
  }

  public Booking getById(long id) {
    Booking booking = bookingRep.findById(id).orElseThrow(
        () -> new RuntimeException("Booking không tồn tại"));
    return booking;
  }

  public ResultPaginationDTO getAllBooking(Specification<Booking> spec,
                                           Pageable pageable) {
    Page<Booking> pageUser = bookingRep.findAll(spec, pageable);
    Meta meta = new Meta();
    meta.setPageCurrent(pageable.getPageNumber() + 1);
    meta.setPageSize(pageable.getPageSize());
    meta.setPages(pageUser.getTotalPages());
    meta.setTotal(pageUser.getTotalElements());

    ResultPaginationDTO result = new ResultPaginationDTO();
    result.setMeta(meta);

    List<BookingResponseDTO> listUser = pageUser.getContent()
                                            .stream()
                                            .map(BookingResponseDTO::new)
                                            .collect(Collectors.toList());
    result.setResult(listUser);

    return result;
  }

  public boolean isIdExist(long id) { return bookingRep.existsById(id); }

  public List<Booking> getBookingByUser(User user) {

    return this.bookingRep.findByUser(user);
  }
}
