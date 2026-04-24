package com.J2EE.TourManagement.Model.DTO.TourDetail;

import com.J2EE.TourManagement.Model.DTO.TourPrice.TourPriceUpdateDTO;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TourDetailUpdateDTO {

    private Long tourId;

    private String startLocation;

    private LocalDate startDay;

    @FutureOrPresent(message = "Ngày kết thúc phải là hiện tại hoặc trong tương lai")
    private LocalDate endDay;

    private Integer capacity;
    private Integer remainingSeats;


    @Pattern(regexp = "ACTIVE|INACTIVE|DRAFT|FULL", message = "Trạng thái phải là ACTIVE, INACTIVE, DRAFT hoặc FULL")
    private String status;
}