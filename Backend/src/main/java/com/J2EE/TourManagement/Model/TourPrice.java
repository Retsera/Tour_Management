package com.J2EE.TourManagement.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "tour_prices")
@Getter
@Setter
public class TourPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tour_detail_id")
    @JsonBackReference(value = "detail-price")
    private TourDetail tourDetail;

    @Column(name = "priceType")
    @NotNull()
    @NotBlank(message = "Loại giá không được để trống")
    @Pattern(regexp = "ADULT|CHILD|INFANT|GROUP", message = "Loại giá phải là ADULT, CHILD, INFANT hoặc GROUP")
    private String priceType;


    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private BigDecimal price;

}
