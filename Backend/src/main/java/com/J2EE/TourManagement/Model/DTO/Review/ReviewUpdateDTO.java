package com.J2EE.TourManagement.Model.DTO.Review;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewUpdateDTO {
    private Long tourId;

    @Size(max = 1000, message = "Nội dung đánh giá không được vượt quá 1000 ký tự")
    private String content;

    @Min(value = 1, message = "Điểm tối thiểu là 1")
    @Max(value = 5, message = "Điểm tối đa là 5")
    private Integer rating;

    private String imageUrl;
}