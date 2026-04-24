package com.J2EE.TourManagement.Model.DTO;

import jakarta.validation.constraints.NotBlank;

public class PaymentDTO {
    
    private long id_booking;

   
    public long getId_booking() {
        return this.id_booking;
    }

    public void setId_booking(long id_booking) {
        this.id_booking = id_booking;
    }


}
