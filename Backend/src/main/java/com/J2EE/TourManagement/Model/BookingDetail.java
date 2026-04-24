package com.J2EE.TourManagement.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "bookingdetail")
public class BookingDetail {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;

  @ManyToOne
  @JoinColumn(name = "id_booking", nullable = false)
  @JsonBackReference
  private Booking booking;

  @ManyToOne
  @JoinColumn(name = "id_tour_detail", nullable = false)
  private TourDetail tourDetail;


  @ManyToOne
  @JoinColumn(name = "id_tourPrice", nullable = false)
  private TourPrice tourPrice;

  private boolean status;

  private int quantity;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "#,###.##")
  private double price;

  public long getId() { return this.id; }

  public void setId(long id) { this.id = id; }

  public Booking getBooking() { return this.booking; }

  public void setBooking(Booking booking) { this.booking = booking; }

  public TourDetail getTourDetail() { return this.tourDetail; }

  public void setTourDetail(TourDetail tourDetail) {
    this.tourDetail = tourDetail;
  }

  public TourPrice getTourPrice() { return this.tourPrice; }

  public void setTourPrice(TourPrice tourPrice) {
    this.tourPrice = tourPrice;
  }

  public boolean isStatus() { return this.status; }

  public boolean getStatus() { return this.status; }

  public void setStatus(boolean status) { this.status = status; }

  public int getQuantity() { return this.quantity; }

  public void setQuantity(int quantity) { this.quantity = quantity; }

  public double getPrice() { return this.price; }

  public void setPrice(double price) { this.price = price; }
}
