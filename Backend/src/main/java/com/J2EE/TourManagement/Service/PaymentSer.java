package com.J2EE.TourManagement.Service;

import com.J2EE.TourManagement.Model.Booking;
import com.J2EE.TourManagement.Model.DTO.PaymentDTO;
import com.J2EE.TourManagement.Model.Payment;
import com.J2EE.TourManagement.Model.PaymentOrder;
import com.J2EE.TourManagement.Repository.BookingRep;
import com.J2EE.TourManagement.Repository.PaymentOrderRepository;
import com.J2EE.TourManagement.Repository.PaymentRep;
import com.J2EE.TourManagement.Util.VNPayConfig;
import com.J2EE.TourManagement.Util.constan.EnumStatusBooking;
import com.J2EE.TourManagement.Util.constan.EnumStatusPayment;
import com.J2EE.TourManagement.Util.error.InvalidException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentSer {
  private final PaymentRep paymentRep;
  private final BookingRep bookingRep;
  private final PaymentOrderRepository paymentOrderRepository;

  @Value("${vnpay.tmnCode}") private String vnp_TmnCode;

  @Value("${vnpay.hashSecret}") private String vnp_HashSecret;

  @Value("${vnpay.url}") private String vnp_Url;

  @Value("${vnpay.returnUrl}") private String vnp_ReturnUrl;

  public PaymentSer(PaymentRep paymentRep, BookingRep bookingRep, PaymentOrderRepository paymentOrderRepository) {
    this.paymentRep = paymentRep;
    this.bookingRep = bookingRep;
      this.paymentOrderRepository = paymentOrderRepository;
  }

  public Payment createPaymentCash(PaymentDTO paymentDTO)
      throws InvalidException {
    Booking booking =
        this.bookingRep.findById(paymentDTO.getId_booking()).isPresent()
            ? this.bookingRep.findById(paymentDTO.getId_booking()).get()
            : null;
    Payment payment = new Payment();
    if (booking.getPayment() != null) {
      throw new InvalidException("Booking đã thanh toán. ");
    }
    booking.setStatus(EnumStatusBooking.valueOf("COMPLETED"));
    payment.setBooking(booking);
    payment.setAmount(booking.getTotalPrice());
    payment.setMethod("Cash");
    payment.setStatus(EnumStatusPayment.valueOf("SUCCESS"));
    return this.paymentRep.save(payment);
  }

  public Payment getPaymentById(long id) {
    Payment Payment = this.paymentRep.findById(id).isPresent()
                          ? this.paymentRep.findById(id).get()
                          : null;
    return Payment;
  }

  public Payment getPaymentByBookingId(long bookingId) throws InvalidException {
      Payment payment = this.paymentRep.findByBookingId(bookingId)
              .orElseThrow(() -> new InvalidException("Không tìm thấy payment cho bookingId: " + bookingId));
      return payment;
  }

  public boolean isIdExist(long id) { return this.paymentRep.existsById(id); }

  public String createVNPayPayment(long bookingId)
          throws UnsupportedEncodingException, InvalidException {
      Booking booking = bookingRep.findById(bookingId)
              .orElseThrow(() -> new InvalidException("Không tìm thấy booking"));

      PaymentOrder order = new PaymentOrder();
      order.setOrderCode(VNPayConfig.getRandomNumber(8));
      order.setUserId(booking.getUser().getId());
      order.setAmount(booking.getTotalPrice());
      order.setStatus(EnumStatusPayment.PENDING);
      paymentOrderRepository.save(order);


      Map<String, String> params = new HashMap<>();
      params.put("vnp_Version", "2.1.0");
      params.put("vnp_Command", "pay");
      params.put("vnp_TmnCode", vnp_TmnCode);
      params.put("vnp_Amount", String.valueOf((long)(order.getAmount() * 100)));
      params.put("vnp_TxnRef", order.getOrderCode());
      params.put("vnp_OrderInfo", "Thanh toan don hang " + order.getOrderCode());
      params.put("vnp_OrderType", "other");
      params.put("vnp_Locale", "vn");
      params.put("vnp_ReturnUrl", vnp_ReturnUrl);
      params.put("vnp_IpAddr", "127.0.0.1");

      String query = VNPayConfig.buildUrl(params);
      String secureHash = hmacSHA512(vnp_HashSecret, query);

      return vnp_Url + "?" + query + "&vnp_SecureHash=" + secureHash;
  }

  private String hmacSHA512(String key, String data) {
    try {
      javax.crypto.Mac hmac512 = javax.crypto.Mac.getInstance("HmacSHA512");
      javax.crypto.spec.SecretKeySpec secretKey =
          new javax.crypto.spec.SecretKeySpec(key.getBytes("UTF-8"),
                                              "HmacSHA512");
      hmac512.init(secretKey);
      byte[] bytes = hmac512.doFinal(data.getBytes("UTF-8"));
      StringBuilder hash = new StringBuilder();
      for (byte b : bytes) {
        hash.append(String.format("%02x", b));
      }
      return hash.toString();
    } catch (Exception ex) {
      throw new RuntimeException("Lỗi khi tạo chữ ký VNPay", ex);
    }
  }
    public Payment createPaymentAfterSuccess(Long userId, String orderCode)
            throws InvalidException {

        PaymentOrder order = paymentOrderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new InvalidException("Order không tồn tại"));

        Booking booking = bookingRep.findByUserIdAndStatus(userId, EnumStatusBooking.PENDING)
                .orElseThrow(() -> new InvalidException("Không tìm thấy booking PENDING"));

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(order.getAmount());
        payment.setMethod("VNPAY");
        payment.setStatus(EnumStatusPayment.SUCCESS);

        paymentRep.save(payment);

        booking.setStatus(EnumStatusBooking.COMPLETED);
        booking.setPayment(payment);
        bookingRep.save(booking);

        return payment;
    }

    public Payment handlePaymentReturn(Map<String, String> params) throws InvalidException {
        String orderCode = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode"); // 00 = success

        PaymentOrder order = paymentOrderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new InvalidException("Không thấy Order"));

        if (responseCode.equals("00")) {
            // Thanh toán thành công
            order.setStatus(EnumStatusPayment.SUCCESS);
            paymentOrderRepository.save(order);

            return createPaymentAfterSuccess(order.getUserId(), orderCode);
        }

        // Thanh toán thất bại
        order.setStatus(EnumStatusPayment.FAILED);
        paymentOrderRepository.save(order);

        return null;
    }
}
