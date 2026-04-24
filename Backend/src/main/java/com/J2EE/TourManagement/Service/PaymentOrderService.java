package com.J2EE.TourManagement.Service;

import com.J2EE.TourManagement.Model.Booking;
import com.J2EE.TourManagement.Model.Payment;
import com.J2EE.TourManagement.Model.PaymentOrder;
import com.J2EE.TourManagement.Repository.BookingRep;
import com.J2EE.TourManagement.Repository.PaymentOrderRepository;
import com.J2EE.TourManagement.Repository.PaymentRep;
import com.J2EE.TourManagement.Util.VNPayConfig;
import com.J2EE.TourManagement.Util.constan.EnumStatusBooking;
import com.J2EE.TourManagement.Util.constan.EnumStatusPayment;
import com.J2EE.TourManagement.Util.error.InvalidException;
import jakarta.transaction.Transactional;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service

public class PaymentOrderService {

  private final PaymentOrderRepository paymentOrderRepository;

  private final BookingRep bookingRepo;
  private final EmailService emailService;
  private final PaymentRep paymentRepo;
  private final PdfService pdfService;

  @Value("${vnpay.tmnCode}") private String vnp_TmnCode;

  @Value("${vnpay.hashSecret}") private String vnp_HashSecret;

  @Value("${vnpay.url}") private String vnp_Url;

  @Value("${vnpay.returnUrl}") private String vnp_ReturnUrl;

  public PaymentOrderService(PaymentOrderRepository paymentOrderRepository,
                             BookingRep bookingRepo, PaymentRep paymentRepo,
                             EmailService emailService, PdfService pdfService) {
    this.paymentOrderRepository = paymentOrderRepository;
    this.bookingRepo = bookingRepo;
    this.emailService = emailService;
    this.paymentRepo = paymentRepo;
    this.pdfService = pdfService;
  }

  @Transactional
  public PaymentOrder createPendingOrder(Long userId, double amount) {
    PaymentOrder request = new PaymentOrder();
    request.setOrderCode(generateOrderCode());
    request.setUserId(userId);
    request.setAmount(amount);
    request.setStatus(EnumStatusPayment.PENDING);
    request.setCreatedAt(LocalDateTime.now());
    request.setUpdatedAt(LocalDateTime.now());
    return paymentOrderRepository.save(request);
  }

  public Optional<PaymentOrder> getOrderByCode(String orderCode) {
    return paymentOrderRepository.findByOrderCode(orderCode);
  }

  @Transactional
  public void markOrderSuccess(String orderCode) {
    PaymentOrder order =
        paymentOrderRepository.findByOrderCode(orderCode).orElseThrow(
            () -> new RuntimeException("Order not found"));

    order.setStatus(EnumStatusPayment.SUCCESS);
    order.setUpdatedAt(LocalDateTime.now());
    paymentOrderRepository.save(order);
  }

  public PaymentOrder createOrder(Long userId) throws InvalidException {

    Booking booking =
        bookingRepo.findByUserIdAndStatus(userId, EnumStatusBooking.PENDING)
            .orElseThrow(
                () -> new InvalidException("Không có booking PENDING"));

    PaymentOrder order = new PaymentOrder();
    order.setOrderCode(generateOrderCode());
    order.setUserId(userId);
    order.setAmount(booking.getTotalPrice());
    order.setStatus(EnumStatusPayment.PENDING);
    order.setCreatedAt(LocalDateTime.now());

    paymentOrderRepository.save(order);

    // Gắn orderCode vào booking
    booking.setOrderCode(order.getOrderCode());
    bookingRepo.save(booking);

    return order;
  }

  private String generateOrderCode() {
    return "ORD" + System.currentTimeMillis();
  }

  public String buildVNPayUrl(PaymentOrder order)
      throws UnsupportedEncodingException {

    Map<String, String> params = new HashMap<>();
    params.put("vnp_Version", "2.1.0");
    params.put("vnp_Command", "pay");
    params.put("vnp_TmnCode", vnp_TmnCode);
    params.put("vnp_Amount", String.valueOf((long)order.getAmount() * 100));
    params.put("vnp_CurrCode", "VND");
    params.put("vnp_TxnRef", order.getOrderCode());
    params.put("vnp_OrderInfo", "Thanh toan don hang " + order.getOrderCode());
    params.put("vnp_OrderType", "billpayment");
    params.put("vnp_Locale", "vn");
    params.put("vnp_ReturnUrl", vnp_ReturnUrl);
    params.put("vnp_IpAddr", "127.0.0.1");

    Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
    String createDate =
        new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(cld.getTime());
    params.put("vnp_CreateDate", createDate);

    String query = VNPayConfig.buildUrl(params);
    String secureHash = hmacSHA512(vnp_HashSecret, query);

    return vnp_Url + "?" + query + "&vnp_SecureHash=" + secureHash;
  }

  private String hmacSHA512(String key, String data) {
    try {
      javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA512");
      javax.crypto.spec.SecretKeySpec secretKey =
          new javax.crypto.spec.SecretKeySpec(key.getBytes("UTF-8"),
                                              "HmacSHA512");
      mac.init(secretKey);

      byte[] bytes = mac.doFinal(data.getBytes("UTF-8"));

      StringBuilder hash = new StringBuilder();
      for (byte b : bytes) {
        hash.append(String.format("%02x", b));
      }
      return hash.toString();

    } catch (Exception e) {
      throw new RuntimeException("Lỗi tạo hash HmacSHA512");
    }
  }

  @Transactional
  public void markOrderCanceled(String orderCode) {
    PaymentOrder order =
        paymentOrderRepository.findByOrderCode(orderCode).orElseThrow(
            () -> new RuntimeException("Order not found"));

    order.setStatus(EnumStatusPayment.CANCELED);
    order.setUpdatedAt(LocalDateTime.now());
    paymentOrderRepository.save(order);
  }

  @Transactional
  public int cancelExpiredOrders(int minutes) {
    LocalDateTime cutoff = LocalDateTime.now().minusMinutes(minutes);

    var expiredOrders =
        paymentOrderRepository.findAllByStatusAndCreatedAtBefore(
            EnumStatusPayment.PENDING, cutoff);

    expiredOrders.forEach(order -> {
      order.setStatus(EnumStatusPayment.CANCELED);
      order.setUpdatedAt(LocalDateTime.now());
    });

    paymentOrderRepository.saveAll(expiredOrders);
    return expiredOrders.size();
  }

  public Payment processVNPaySuccess(String orderCode) throws Exception {

    PaymentOrder order =
        paymentOrderRepository.findByOrderCode(orderCode).orElseThrow(
            () -> new InvalidException("Order không tồn tại"));

    if (order.getStatus() == EnumStatusPayment.SUCCESS) {
      throw new InvalidException("Order đã được thanh toán rồi");
    }

    Booking booking =
        bookingRepo
            .findByUserIdAndStatus(order.getUserId(), EnumStatusBooking.PENDING)
            .orElseThrow(
                () -> new InvalidException("Không tìm thấy booking phù hợp"));

    // Tạo Payment
    Payment payment = new Payment();
    payment.setBooking(booking);
    payment.setAmount(order.getAmount());
    payment.setMethod("VNPAY");
    payment.setStatus(EnumStatusPayment.SUCCESS);
    paymentRepo.save(payment);

    // Update booking
    booking.setStatus(EnumStatusBooking.COMPLETED);
    booking.setPayment(payment);
    booking.setOrderCode(order.getOrderCode());
    bookingRepo.save(booking);

    // Update order
    order.setStatus(EnumStatusPayment.SUCCESS);
    paymentOrderRepository.save(order);

    // hàm in file PDF
    byte[] pdfBytes = this.pdfService.generateBookingPDFBytes(booking);
    // Gửi email thông báo thành công
    String userEmail =
        payment.getBooking().getContactEmail(); // giả sử booking có user
    String subject = "Thanh toán thành công - Đơn hàng " + orderCode;
    String body = "Xin chào " + booking.getUser().getFullname() + ",\n\n"
                  + "Bạn đã thanh toán thành công đơn hàng #" + orderCode +
                  ".\n"
                  + "Booking ID: " + booking.getId() + "\n"
                  + "Hóa đơn chi tiết được đính kèm trong file PDF.\n\n"
                  + "Cảm ơn bạn đã sử dụng dịch vụ!";

    // Gửi email có đính kèm PDF
    emailService.sendPaymentSuccessEmailWithAttachment(
        userEmail, subject, body, pdfBytes,
        "HoaDon_Booking_" + booking.getId() + ".pdf");

    return payment;
  }
}
