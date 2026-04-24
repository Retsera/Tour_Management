package com.J2EE.TourManagement.Model.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BookingDetailDTO {

    @NotNull(message = "TourDetailId không được để trống.")
    private Long tourDetailId;

    @NotNull(message = "TourPriceId không được để trống.")
    private Long tourPriceId;

    @Min(value = 1, message = "Số lượng phải lớn hơn 0.")
    private int quantity;

    @Min(value = 0, message = "Giá không được âm.")
    private double price;

    // Getters & Setters
    public Long getTourDetailId() {
        return tourDetailId;
    }

    public void setTourDetailId(Long tourDetailId) {
        this.tourDetailId = tourDetailId;
    }

    public Long getTourPriceId() {
        return tourPriceId;
    }

    public void setTourPriceId(Long tourPriceId) {
        this.tourPriceId = tourPriceId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
