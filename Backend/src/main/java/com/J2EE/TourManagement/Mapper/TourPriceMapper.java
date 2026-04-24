package com.J2EE.TourManagement.Mapper;

import com.J2EE.TourManagement.Model.DTO.Tour.TourUpdateDTO;
import com.J2EE.TourManagement.Model.DTO.TourPrice.TourPriceCreateDTO;
import com.J2EE.TourManagement.Model.DTO.TourPrice.TourPriceCreateWithTourDetailDTO;
import com.J2EE.TourManagement.Model.DTO.TourPrice.TourPriceUpdateDTO;
import com.J2EE.TourManagement.Model.Tour;
import com.J2EE.TourManagement.Model.TourPrice;
import com.J2EE.TourManagement.Model.DTO.TourPrice.TourPriceDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TourPriceMapper {
    TourPrice toEntity(TourPriceCreateDTO tourPriceCreateDTO);

    TourPrice toEntityWithDetail(TourPriceCreateWithTourDetailDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(TourPriceUpdateDTO dto, @MappingTarget TourPrice entity);

    TourPriceDTO toDTO(TourPrice tourPrice);

    List<TourPriceDTO> toDTOList(List<TourPrice> tourPrice);
}
