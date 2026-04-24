package com.J2EE.TourManagement.Model.DTO.Tour;

import com.J2EE.TourManagement.Model.DTO.Review.ReviewDTO;
import com.J2EE.TourManagement.Model.DTO.TourDetail.TourDetailDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Getter
@Setter
public class TourDTO {
    private Long id;
    private String title;
    private String shortDesc;
    private String longDesc;
    private String duration;
    private Integer capacity;
    private String location;
    private String status;
    private String imageUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;

    private List<TourDetailDTO> tourDetails;
    private List<ReviewDTO> reviews;
}
