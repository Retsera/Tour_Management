package com.J2EE.TourManagement.Mapper;

import com.J2EE.TourManagement.Model.DTO.TourItinerary.*;
import com.J2EE.TourManagement.Model.TourItinerary;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TourItineraryMapper {

    // Entity -> ResponseDTO
    TourItineraryDTO toResponseDTO(TourItinerary itinerary);

    List<TourItineraryDTO> toResponseDTOList(List<TourItinerary> itineraries);

    // CreateDTO -> Entity
    @Mapping(target = "tourDetail", ignore = true)
    TourItinerary toEntity(TourItineraryCreateDTO dto);

    @Mapping(target = "tourDetail", ignore = true)
    TourItinerary toEntityWithDetail(TourItineraryCreateWithTourDetailDTO dto);

    // UpdateDTO -> cập nhật entity hiện có
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(TourItineraryUpdateDTO dto, @MappingTarget TourItinerary itinerary);
}
