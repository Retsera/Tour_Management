package com.J2EE.TourManagement.Service;

import com.J2EE.TourManagement.Model.Booking;
import com.J2EE.TourManagement.Model.BookingDetail;
import com.J2EE.TourManagement.Model.Payment;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import org.springframework.stereotype.Service;

@Service
public class PdfService {

  // Sinh file PDF trên server
  public File generateBookingPDF(Booking booking) throws Exception {
    String fileName = "Booking_" + booking.getId() + ".pdf";
    Document document = new Document();
    PdfWriter.getInstance(document, new FileOutputStream(fileName));
    document.open();

    // Tiêu đề
    Paragraph title = new Paragraph(
        "BOOKING", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
    title.setAlignment(Element.ALIGN_CENTER);
    document.add(title);
    document.add(new Paragraph(" ")); // dòng trống

    // Thông tin booking
    document.add(new Paragraph("Booking ID: " + booking.getId()));
    document.add(new Paragraph("Order Code: " + booking.getOrderCode()));
    document.add(new Paragraph("Status: " + booking.getStatus()));
    document.add(new Paragraph("Created At: " + booking.getCreatedAt()));
    document.add(new Paragraph("Expired At: " + booking.getExpiredAt()));
    document.add(new Paragraph(" "));

    // Thông tin liên hệ
    document.add(new Paragraph("Thông tin liên hệ:"));
    document.add(new Paragraph("Họ tên: " + booking.getContactFullname()));
    document.add(new Paragraph("Email: " + booking.getContactEmail()));
    document.add(new Paragraph("Phone: " + booking.getContactPhone()));
    document.add(new Paragraph("Address: " + booking.getContactAddress()));
    document.add(new Paragraph(" "));

    // Payment
    Payment payment = booking.getPayment();
    if (payment != null) {
      document.add(new Paragraph("Thông tin thanh toán:"));
      document.add(new Paragraph("Payment ID: " + payment.getId()));
      document.add(new Paragraph("Amount: " + payment.getAmount()));
      document.add(new Paragraph("Method: " + payment.getMethod()));
      document.add(new Paragraph("Status: " + payment.getStatus()));
      document.add(new Paragraph(" "));
    }

    // Chi tiết booking
    if (booking.getBookingDetails() != null &&
        !booking.getBookingDetails().isEmpty()) {
      document.add(new Paragraph("Chi tiết booking:"));
      for (BookingDetail detail : booking.getBookingDetails()) {
        document.add(new Paragraph(
            "- Tour: " + detail.getTourDetail().getTour().getLocation() +
            ", Số lượng: " + detail.getQuantity() +
            ", Giá: " + detail.getPrice()));
      }
    }

    document.close();
    return new File(fileName);
  }

  // Nếu muốn trả về byte[] để gửi email mà không lưu file
  public byte[] generateBookingPDFBytes(Booking booking) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Document document = new Document();
    PdfWriter.getInstance(document, baos);
    document.open();

    document.add(new Paragraph("===== THÔNG TIN BOOKING ====="));
    document.add(new Paragraph("Booking ID: " + booking.getId()));
    document.add(new Paragraph("Order Code: " + booking.getOrderCode()));
    document.add(new Paragraph("Status: " + booking.getStatus()));
    document.add(
        new Paragraph("Total Price: " + booking.getTotalPrice() + " VND"));
    document.add(new Paragraph("\n"));

    document.add(new Paragraph("===== THÔNG TIN NGƯỜI ĐẶT ====="));
    document.add(new Paragraph("User ID: " + booking.getUser().getId()));
    document.add(
        new Paragraph("Họ tên user: " + booking.getUser().getFullname()));
    document.add(new Paragraph("Email user: " + booking.getUser().getEmail()));
    document.add(new Paragraph("\n"));

    document.add(new Paragraph("===== THÔNG TIN LIÊN HỆ ====="));
    document.add(
        new Paragraph("Họ tên liên hệ: " + booking.getContactFullname()));
    document.add(new Paragraph("Email liên hệ: " + booking.getContactEmail()));
    document.add(
        new Paragraph("Số điện thoại liên hệ: " + booking.getContactPhone()));
    document.add(
        new Paragraph("Địa chỉ liên hệ: " + booking.getContactAddress()));
    document.add(new Paragraph("\n"));

    document.add(new Paragraph("===== THỜI GIAN ====="));
    document.add(new Paragraph("Created At: " + booking.getCreatedAt()));
    document.add(new Paragraph("Updated At: " + booking.getUpdatedAt()));
    document.add(new Paragraph("Expired At: " + booking.getExpiredAt()));
    document.add(new Paragraph("\n"));

    document.add(new Paragraph("===== GHI CHÚ ====="));
    document.add(
        new Paragraph("Note: " + (booking.getNote() != null ? booking.getNote()
                                                            : "Không có")));
    document.add(new Paragraph("\n"));

    document.add(new Paragraph("===== CHI TIẾT BOOKING ====="));

    if (booking.getBookingDetails() != null &&
        !booking.getBookingDetails().isEmpty()) {

      PdfPTable table = new PdfPTable(5); // cột
      table.setWidthPercentage(100);

      table.addCell("ID");
      table.addCell("Tour");
      table.addCell("Giá");
      table.addCell("Số lượng");
      table.addCell("Tổng");

      for (BookingDetail d : booking.getBookingDetails()) {
        table.addCell(String.valueOf(d.getId()));
        table.addCell(d.getTourDetail().getTour().getLocation());
        table.addCell(String.valueOf(d.getPrice()));
        table.addCell(String.valueOf(d.getQuantity()));
        table.addCell(String.valueOf(d.getPrice()));
      }

      document.add(table);

    } else {
      document.add(new Paragraph("Không có chi tiết booking"));
    }

    document.add(new Paragraph("\n===== THANH TOÁN ====="));
    if (booking.getPayment() != null) {
      document.add(
          new Paragraph("Payment ID: " + booking.getPayment().getId()));
      document.add(
          new Paragraph("Amount: " + booking.getPayment().getAmount()));
      document.add(
          new Paragraph("Method: " + booking.getPayment().getMethod()));
      document.add(
          new Paragraph("Status: " + booking.getPayment().getStatus()));
    } else {
      document.add(new Paragraph("Chưa thanh toán"));
    }

    document.add(new Paragraph("\n===== KẾT THÚC HÓA ĐƠN ====="));

    document.close();
    return baos.toByteArray();
  }
}
