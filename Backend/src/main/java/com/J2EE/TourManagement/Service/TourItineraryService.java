package com.J2EE.TourManagement.Service;

import com.J2EE.TourManagement.Mapper.TourItineraryMapper;
import com.J2EE.TourManagement.Model.DTO.ResultPaginationDTO;
import com.J2EE.TourManagement.Model.DTO.TourItinerary.TourItineraryCreateDTO;
import com.J2EE.TourManagement.Model.DTO.TourItinerary.TourItineraryDTO;
import com.J2EE.TourManagement.Model.DTO.TourItinerary.TourItineraryUpdateDTO;
import com.J2EE.TourManagement.Model.TourDetail;
import com.J2EE.TourManagement.Model.TourItinerary;
import com.J2EE.TourManagement.Model.TourPrice;
import com.J2EE.TourManagement.Repository.TourDetailRepository;
import com.J2EE.TourManagement.Repository.TourItineraryRepository;
import com.J2EE.TourManagement.Util.error.InvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TourItineraryService {
    private final TourItineraryRepository tourItineraryRepository;
    private final TourItineraryMapper tourItineraryMapper;
    private final TourDetailRepository tourDetailRepository;

    //Create
    public TourItinerary handleSave(TourItineraryCreateDTO dto) throws InvalidException {
        // 1. Tìm TourDetail cha
        TourDetail tourDetail = tourDetailRepository.findById(dto.getTourDetailId())
                .orElseThrow(() -> new InvalidException("Không tìm thấy TourDetail với id = " + dto.getTourDetailId()));

        TourItinerary itinerary = tourItineraryMapper.toEntity(dto);

        itinerary.setTourDetail(tourDetail);

        return tourItineraryRepository.save(itinerary);
    }

    //Read all
    public ResultPaginationDTO handleGetAll(Specification<TourItinerary> spec, Pageable pageable) {
        Page<TourItinerary> tourItineraries = tourItineraryRepository.findAll(spec, pageable);
        Page<TourItineraryDTO> dtoPage = tourItineraries.map(tourItineraryMapper::toResponseDTO);
        return PaginationUtils.build(dtoPage, pageable);
    }

    //Update
    public TourItinerary handleUpdate(Long id, TourItineraryUpdateDTO dto) throws InvalidException {
        TourItinerary existing = tourItineraryRepository.findById(id)
                .orElseThrow(() -> new InvalidException("Không tìm thấy TourItinerary để cập nhật (id = " + id + ")"));

        // 1. Map dữ liệu mới vào entity cũ
        tourItineraryMapper.updateEntityFromDTO(dto, existing);

        // 2. Nếu có thay đổi Tour cha (chuyển lộ trình này sang tour khác)
        if (dto.getTourDetailId() != null) {
            TourDetail detail = tourDetailRepository.findById(dto.getTourDetailId())
                    .orElseThrow(() -> new InvalidException(
                            "Không tìm thấy TourDetail với id = " + dto.getTourDetailId()));


            existing.setTourDetail(detail);
        }

        return tourItineraryRepository.save(existing);
    }

    // Delete
    public void handleDelete(Long id) throws InvalidException {
        TourItinerary existing = tourItineraryRepository.findById(id)
                .orElseThrow(() -> new InvalidException("Không  tìm thấy TourItinerary với id = " + id));

        // Gỡ TourItinerary khỏi danh sách trong TourDetail
        TourDetail detail = existing.getTourDetail();
        if (detail != null && detail.getItineraries() != null) {
            detail.getItineraries().remove(existing);
        }

        tourItineraryRepository.delete(existing);
    }
}
