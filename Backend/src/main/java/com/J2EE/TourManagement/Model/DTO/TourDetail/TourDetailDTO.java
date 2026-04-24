package com.J2EE.TourManagement.Model.DTO.TourDetail;

import com.J2EE.TourManagement.Model.DTO.Review.ReviewDTO;
import com.J2EE.TourManagement.Model.DTO.TourPrice.TourPriceDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.J2EE.TourManagement.Model.DTO.TourItinerary.TourItineraryDTO;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class TourDetailDTO {

    private Long id;

    private String startLocation;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+7")
    private LocalDate startDay;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+7")
    private LocalDate endDay;

    private Integer capacity;

    private Integer remainingSeats;

    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;

    // Danh sách giá (TourPrice)
    private List<TourPriceDTO> tourPrices;

    // Itinerary
    private List<TourItineraryDTO> itineraries;
}