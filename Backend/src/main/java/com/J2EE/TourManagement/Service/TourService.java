package com.J2EE.TourManagement.Service;

import com.J2EE.TourManagement.Mapper.TourMapper;
import com.J2EE.TourManagement.Model.DTO.ResultPaginationDTO;
import com.J2EE.TourManagement.Model.DTO.Tour.TourDTO;
import com.J2EE.TourManagement.Model.DTO.Tour.TourUpdateDTO;
import com.J2EE.TourManagement.Util.error.InvalidException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;

import com.J2EE.TourManagement.Model.Tour;
import com.J2EE.TourManagement.Repository.TourRepository;
import com.J2EE.TourManagement.Util.error.InvalidException;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class TourService {
    private final TourRepository tourRepository;
    private final TourMapper tourMapper;

    //Create
    @Transactional
    public Tour handleSave(Tour tour) {
        return tourRepository.save(tour);
    }


    // Read
    public ResultPaginationDTO handleGetAll(Specification<Tour> spec,
                                            Pageable pageable) {
        Page<Tour> tours = tourRepository.findAll(spec, pageable);

        Page<TourDTO> dtoPage = tours.map(tourMapper::toDTO);

        return PaginationUtils.build(dtoPage, pageable);
    }

    @Transactional()
    public Tour handleUpdateStatus(Long id, Tour tourRequest)
            throws InvalidException {
        // 1️⃣ Tìm tour theo id
        Tour existingTour = tourRepository.findById(id).orElseThrow(
                () -> new InvalidException("Tour không tồn tại"));

        // 2️⃣ Cập nhật trạng thái
        existingTour.setStatus(tourRequest.getStatus());

        // 3️⃣ Lưu thay đổi
        return tourRepository.save(existingTour);
    }

    // xoa
    public void handleDelete(Long id) {
        if (!this.tourRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy tour để xóa (id = " + id +
                    ")");
        }
        this.tourRepository.deleteById(id);
    }

    //Update
    public Tour handleUpdate(Long id, TourUpdateDTO dto) throws InvalidException {
        Tour existingTour = tourRepository.findById(id)
                .orElseThrow(() -> new InvalidException("Không tìm thấy Tour để cập nhật (id = " + id + ")"));

        // Map dữ liệu từ DTO sang entity có sẵn
        tourMapper.updateEntityFromDto(dto, existingTour);

        return tourRepository.save(existingTour);
    }

    //get by id
    public Tour handleGetById(Long id)
            throws InvalidException {
        return tourRepository.findById(id)
                .orElseThrow(() -> new InvalidException("Tour không tồn tại id = " + id + ""));
    }

    public boolean checkIdExists(long id) {
        return this.tourRepository.existsById(id);
    }

    public List<Tour> searchTours(String location, String startLocation, Integer remainingSeats) {
        return tourRepository.searchTours(location, startLocation, remainingSeats);
    }

}
