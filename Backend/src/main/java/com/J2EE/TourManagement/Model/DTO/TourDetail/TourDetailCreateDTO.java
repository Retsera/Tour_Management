package com.J2EE.TourManagement.Model.DTO.TourDetail;

import com.J2EE.TourManagement.Model.DTO.Review.ReviewCreateDTO;
import com.J2EE.TourManagement.Model.DTO.TourItinerary.TourItineraryCreateDTO;
import com.J2EE.TourManagement.Model.DTO.TourItinerary.TourItineraryCreateWithTourDetailDTO;
import com.J2EE.TourManagement.Model.DTO.TourPrice.TourPriceCreateDTO;
import com.J2EE.TourManagement.Model.DTO.TourPrice.TourPriceCreateWithTourDetailDTO;
import com.J2EE.TourManagement.Model.TourItinerary;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.boot.convert.DataSizeUnit;

import java.time.LocalDate;
import java.util.List;

@Data
public class TourDetailCreateDTO {

    @NotNull(message = "ID tour cha không được để trống")
    private Long tourId;

    @NotBlank(message = "Điểm khởi hành không được để trống")
    private String startLocation;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDay;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @FutureOrPresent(message = "Ngày kết thúc phải là hiện tại hoặc trong tương lai")
    private LocalDate endDay;

    @NotNull(message = "Tổng số chỗ không được để trống")
    @Min(value = 0, message = "Tổng số chỗ không được âm")
    private Integer capacity;

    @NotNull(message = "Số chỗ còn lại không được để trống")
    @Min(value = 0, message = "Số chỗ còn lại không được âm")
    private Integer remainingSeats;

    @Pattern(regexp = "ACTIVE|INACTIVE|DRAFT|FULL", message = "Trạng thái phải là ACTIVE, INACTIVE, DRAFT hoặc FULL")
    private String status;

    @Valid
    private List<TourPriceCreateWithTourDetailDTO> tourPrices;

    @Valid
    private List<TourItineraryCreateWithTourDetailDTO> itineraries;
}