package com.J2EE.TourManagement.Model.DTO.Review;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewCreateDTO {

    @NotNull(message = "ID tour không được để trống")
    private Long tourId;

    @NotBlank(message = "Tên người đánh giá không được để trống")
    @Size(max = 100, message = "Tên người đánh giá không được vượt quá 100 ký tự")
    private String reviewerName;

    @NotBlank(message = "Nội dung đánh giá không được để trống")
    @Size(max = 1000, message = "Nội dung đánh giá không được vượt quá 1000 ký tự")
    private String content;

    @NotNull(message = "Điểm đánh giá không được để trống")
    @Min(value = 1, message = "Điểm tối thiểu là 1")
    @Max(value = 5, message = "Điểm tối đa là 5")
    private Integer rating;

    private String imageUrl;
}