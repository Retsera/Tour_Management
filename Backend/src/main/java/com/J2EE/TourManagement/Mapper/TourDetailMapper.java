package com.J2EE.TourManagement.Mapper;

import com.J2EE.TourManagement.Model.DTO.Tour.TourUpdateDTO;
import com.J2EE.TourManagement.Model.DTO.TourDetail.TourDetailCreateDTO;
import com.J2EE.TourManagement.Model.DTO.TourDetail.TourDetailUpdateDTO;
import com.J2EE.TourManagement.Model.Tour;
import com.J2EE.TourManagement.Model.TourDetail;
import com.J2EE.TourManagement.Model.DTO.TourDetail.TourDetailDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TourPriceMapper.class, TourItineraryMapper.class})
public interface TourDetailMapper {

    TourDetailDTO toDTO(TourDetail tourDetail);

    List<TourDetailDTO> toDTOList(List<TourDetail> tourDetails);

    @Mapping(target = "tour", ignore = true)
    TourDetail toEntity(TourDetailCreateDTO tourDetailCreateDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(TourDetailUpdateDTO dto, @MappingTarget TourDetail entity);
}