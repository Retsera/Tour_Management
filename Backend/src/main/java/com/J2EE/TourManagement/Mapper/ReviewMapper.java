package com.J2EE.TourManagement.Mapper;

import com.J2EE.TourManagement.Model.DTO.Review.*;
import com.J2EE.TourManagement.Model.DTO.TourDetail.TourDetailUpdateDTO;
import com.J2EE.TourManagement.Model.Review;
import com.J2EE.TourManagement.Model.TourDetail;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    Review toEntity(ReviewCreateDTO dto);

    ReviewDTO toResponseDTO(Review review);

    List<ReviewDTO> toResponseDTOList(List<Review> reviews);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ReviewUpdateDTO dto, @MappingTarget Review entity);
}