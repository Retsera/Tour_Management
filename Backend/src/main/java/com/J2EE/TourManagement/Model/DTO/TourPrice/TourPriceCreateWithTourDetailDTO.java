package com.J2EE.TourManagement.Model.DTO.TourPrice;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TourPriceCreateWithTourDetailDTO {

    @NotBlank(message = "Loại giá không được để trống")
    @Pattern(regexp = "ADULT|CHILD|INFANT|GROUP", message = "Loại giá phải là ADULT, CHILD, INFANT hoặc GROUP")
    private String priceType;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private BigDecimal price;
}