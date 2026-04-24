package com.J2EE.TourManagement.Model.DTO.TourItinerary;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TourItineraryUpdateDTO {
    private Long tourDetailId;

    @Size(max = 255, message = "Tiêu đề không được vượt quá 255 ký tự")
    private String title;

    private String content;
}