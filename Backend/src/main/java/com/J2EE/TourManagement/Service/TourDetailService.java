package com.J2EE.TourManagement.Service;

import com.J2EE.TourManagement.Mapper.TourDetailMapper;
import com.J2EE.TourManagement.Mapper.TourItineraryMapper;
import com.J2EE.TourManagement.Mapper.TourPriceMapper;
import com.J2EE.TourManagement.Model.DTO.TourDetail.TourDetailCreateDTO;
import com.J2EE.TourManagement.Model.DTO.TourDetail.TourDetailDTO;
import com.J2EE.TourManagement.Model.DTO.TourDetail.TourDetailUpdateDTO;
import com.J2EE.TourManagement.Model.Tour;
import com.J2EE.TourManagement.Model.TourDetail;
import com.J2EE.TourManagement.Model.TourItinerary;
import com.J2EE.TourManagement.Model.TourPrice;
import com.J2EE.TourManagement.Repository.TourDetailRepository;
import com.J2EE.TourManagement.Repository.TourPriceRepository;
import com.J2EE.TourManagement.Repository.TourRepository;
import com.J2EE.TourManagement.Util.error.InvalidException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TourDetailService {

    private final TourDetailMapper tourDetailMapper;
    private final TourPriceMapper tourPriceMapper;
    private final TourItineraryMapper tourItineraryMapper;
    private final TourRepository tourRepository;
    private final TourDetailRepository tourDetailRepository;
    private final TourPriceRepository tourPriceRepository;

    // Create
    public TourDetail handleSave(TourDetailCreateDTO dto)
            throws InvalidException {
        // Lấy Tour cha
        Tour tour = tourRepository.findById(dto.getTourId())
                .orElseThrow(() -> new InvalidException("Không tìm thấy tour cha"));

        // Map DTO sang entity
        TourDetail tourDetail = tourDetailMapper.toEntity(dto);
        tourDetail.setTour(tour);

        // Map và set TourPrice
        if (dto.getTourPrices() != null) {
            List<TourPrice> prices = dto.getTourPrices().stream()
                    .map(tpDTO -> {
                        TourPrice tp = tourPriceMapper.toEntityWithDetail(tpDTO);
                        tp.setTourDetail(tourDetail); // set parent
                        return tp;
                    })
                    .collect(Collectors.toList());
            tourDetail.setTourPrices(prices);
        }

        // Map và set TourItinerary
        if (dto.getItineraries() != null) {
            List<TourItinerary> itineraries = dto.getItineraries().stream()
                    .map(itDTO -> {
                        TourItinerary it = tourItineraryMapper.toEntityWithDetail(itDTO);
                        it.setTourDetail(tourDetail); // set parent
                        return it;
                    })
                    .collect(Collectors.toList());
            tourDetail.setItineraries(itineraries);
        }

        return tourDetailRepository.save(tourDetail);
    }

    //Read by tour id
    public List<TourDetailDTO> handleGetAll(Long tourId)
            throws InvalidException {

        Optional<Tour> tourOpt = tourRepository.findById(tourId);
        if (tourOpt.isEmpty()) {
            throw new InvalidException(
                    "Không tìm thấy TourId (id = " + tourId + ")");
        }

        List<TourDetail> details = tourOpt.get().getTourDetails();

        // Map entity -> DTO
        return tourDetailMapper.toDTOList(details);
    }

    // Update
    public TourDetail handleUpdate(Long id, TourDetailUpdateDTO dto)
            throws InvalidException {
        TourDetail existing = tourDetailRepository.findById(id)
                .orElseThrow(() -> new InvalidException("Không tìm thấy TourDetail để cập nhật (id = " + id + ")"));

        // Map dữ liệu từ DTO sang entity có sẵn
        tourDetailMapper.updateEntityFromDto(dto, existing);


        // Gán tourid cho tourdetail
        if (dto.getTourId() != null) {
            Tour tour = tourRepository.findById(dto.getTourId())
                    .orElseThrow(() -> new InvalidException(
                            "Không tìm thấy Tour với id = " + dto.getTourId()));
            existing.setTour(tour);
        }

        return tourDetailRepository.save(existing);
    }

    public TourDetail getTourDetailById(long id) {
        TourDetail tourDetail = this.tourDetailRepository.findById(id).isPresent()
                ? this.tourDetailRepository.findById(id).get()
                : null;

        return tourDetail;
    }
}
