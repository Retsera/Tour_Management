package com.J2EE.TourManagement.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingStatisticDTO {

    private long totalBooking;
    private long totalTour;
    private double totalRevenue;
    private long totalTourBooked;
}
