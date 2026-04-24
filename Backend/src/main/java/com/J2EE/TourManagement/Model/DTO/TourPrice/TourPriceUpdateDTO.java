package com.J2EE.TourManagement.Model.DTO.TourPrice;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TourPriceUpdateDTO {

    private Long tourDetailId;

    @Pattern(regexp = "ADULT|CHILD|INFANT|GROUP", message = "Loại giá phải là ADULT, CHILD, INFANT hoặc GROUP")
    private String priceType;

    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private BigDecimal price;
}