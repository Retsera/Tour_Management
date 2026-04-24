package com.J2EE.TourManagement.Model.DTO.TourPrice;


import java.math.BigDecimal;

public class TourPriceDTO {
    private Long id;
    private String priceType;
    private BigDecimal price;

    public TourPriceDTO(Long id, String priceType, BigDecimal price) {
        this.id = id;
        this.priceType = priceType;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}