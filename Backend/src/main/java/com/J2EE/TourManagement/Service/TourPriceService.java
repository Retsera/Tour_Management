package com.J2EE.TourManagement.Service;

import com.J2EE.TourManagement.Mapper.TourPriceMapper;
import com.J2EE.TourManagement.Model.DTO.TourPrice.TourPriceCreateDTO;
import com.J2EE.TourManagement.Model.DTO.TourPrice.TourPriceDTO;
import com.J2EE.TourManagement.Model.DTO.TourPrice.TourPriceUpdateDTO;
import com.J2EE.TourManagement.Model.TourDetail;
import com.J2EE.TourManagement.Model.TourPrice;
import com.J2EE.TourManagement.Repository.TourDetailRepository;
import com.J2EE.TourManagement.Repository.TourPriceRepository;
import com.J2EE.TourManagement.Util.error.InvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TourPriceService {

    private final TourPriceRepository tourPriceRepository;
    private final TourDetailRepository tourDetailRepository;
    private final TourPriceMapper tourPriceMapper;

    //Create
    public TourPrice handleSave(TourPriceCreateDTO dto)
            throws InvalidException {
        TourPrice reponse = tourPriceMapper.toEntity(dto);

        // Gán tourDetail id cho price
        if (dto.getTourDetailId() != null) {
            TourDetail detail = tourDetailRepository.findById(dto.getTourDetailId())
                    .orElseThrow(() -> new InvalidException(
                            "Không tìm thấy TourDetail với id = " + dto.getTourDetailId()));
            reponse.setTourDetail(detail);
        }

        return tourPriceRepository.save(reponse);
    }

    //Read by TourDetail id
    public List<TourPriceDTO> handleGetAll(Long id) throws InvalidException {
        Optional<TourDetail> opt = tourDetailRepository.findById(id);

        if (opt.isEmpty()) {
            throw new InvalidException("Không tìm thấy TourDetailId để getall (id = " + id-- + ")");
        }

        List<TourPrice> price = opt.get().getTourPrices();

        return tourPriceMapper.toDTOList(price);
    }

    //Update
    public TourPrice handleUpdate(Long id, TourPriceUpdateDTO dto)
            throws InvalidException {
        TourPrice existing = tourPriceRepository.findById(id)
                .orElseThrow(() -> new InvalidException("Không tìm thấy TourPrice Id để cập nhật (id = " + id + ")"));

        // Map dữ liệu từ DTO sang entity có sẵn
        tourPriceMapper.updateEntityFromDto(dto, existing);

        // Gán tourDetail id cho price
        if (dto.getTourDetailId() != null) {
            TourDetail detail = tourDetailRepository.findById(dto.getTourDetailId())
                    .orElseThrow(() -> new InvalidException(
                            "Không tìm thấy TourDetail với id = " + dto.getTourDetailId()));
            existing.setTourDetail(detail);
        }

        return tourPriceRepository.save(existing);
    }

    // Delete
    public void handleDelete(Long id)
            throws InvalidException {
        TourPrice existing = tourPriceRepository.findById(id)
                .orElseThrow(() -> new InvalidException("Không  tìm thấy TourPrice với id = " + id));

        // Gỡ TourPrice khỏi danh sách trong TourDetail
        TourDetail detail = existing.getTourDetail();
        if (detail != null && detail.getTourPrices() != null) {
            detail.getTourPrices().remove(existing);
        }

        tourPriceRepository.delete(existing);
    }

    public TourPrice getTourPriceById(long id) {
        TourPrice tourPrice = this.tourPriceRepository.findById(id).isPresent() ? this.tourPriceRepository.findById(id).get() : null;
        return tourPrice;
    }

}
