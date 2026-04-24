package com.J2EE.TourManagement.Model.DTO.TourItinerary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TourItineraryCreateWithTourDetailDTO {

    @NotBlank(message = "Tiêu đề lịch trình không được để trống")
    @Size(max = 255, message = "Tiêu đề không được vượt quá 255 ký tự")
    private String title;

    @NotBlank(message = "Nội dung lịch trình không được để trống")
    private String content;
}