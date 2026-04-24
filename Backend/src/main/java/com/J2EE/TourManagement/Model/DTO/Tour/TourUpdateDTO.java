package com.J2EE.TourManagement.Model.DTO.Tour;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class TourUpdateDTO {
    @Size(max = 255, message = "Tiêu đề không được vượt quá 255 ký tự")
    private String title;

    @Size(max = 500, message = "Mô tả ngắn không được vượt quá 500 ký tự")
    private String shortDesc;

    private String longDesc;

    private String imageUrl;

    @Pattern(regexp = "^\\d+ ngày \\d+ đêm$", message = "Thời lượng phải đúng định dạng: 'X ngày Y đêm'")
    private String duration;

    @Size(max = 255, message = "Điểm đến không được vượt quá 255 ký tự")
    private String location;

    @Pattern(regexp = "ACTIVE|INACTIVE|DRAFT", message = "Trạng thái phải là ACTIVE, INACTIVE hoặc DRAFT")
    private String status;
}
