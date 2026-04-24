package com.J2EE.TourManagement.Service;

import com.J2EE.TourManagement.Mapper.ReviewMapper;
import com.J2EE.TourManagement.Model.DTO.Review.ReviewCreateDTO;
import com.J2EE.TourManagement.Model.DTO.Review.ReviewDTO;
import com.J2EE.TourManagement.Model.DTO.Review.ReviewUpdateDTO;
import com.J2EE.TourManagement.Model.Review;
import com.J2EE.TourManagement.Model.Tour;
import com.J2EE.TourManagement.Model.TourDetail;
import com.J2EE.TourManagement.Model.TourPrice;
import com.J2EE.TourManagement.Repository.ReviewRepository;
import com.J2EE.TourManagement.Repository.TourDetailRepository;
import com.J2EE.TourManagement.Repository.TourRepository;
import com.J2EE.TourManagement.Util.error.InvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final TourRepository tourRepository;

    //Create
    public Review handleSave(ReviewCreateDTO dto)
            throws InvalidException {
        Review review = reviewMapper.toEntity(dto);

        // Gán tour id cho review
        if (dto.getTourId() != null) {
            Tour tour = tourRepository.findById(dto.getTourId())
                    .orElseThrow(() -> new InvalidException(
                            "Không tìm thấy TourDetail với id = " + dto.getTourId()));
            review.setTour(tour);
        }

        return reviewRepository.save(review);
    }

    //Read all by tourDetail id
    public List<Review> handleGetAll(Long id)
            throws InvalidException {

        Optional<Tour> opt = tourRepository.findById(id);
        if (opt.isEmpty()) {
            throw new InvalidException(
                    "Không tìm thấy Tour Id (id = " + id + ")"
            );
        }

        return opt.get().getReviews();
    }

    //Update
    public Review handleUpdate(Long id, ReviewUpdateDTO dto)
            throws InvalidException {
        Review existing = reviewRepository.findById(id)
                .orElseThrow(() -> new InvalidException(
                        "Không tìm thấy Review để cập nhật (id = " + id + ")"));

        reviewMapper.updateEntityFromDto(dto, existing);

        // Gán tour id cho review
        if (dto.getTourId() != null) {
            Tour tour = tourRepository.findById(dto.getTourId())
                    .orElseThrow(() -> new InvalidException(
                            "Không tìm thấy TourDetail với id = " + dto.getTourId()));
            existing.setTour(tour);
        }

        return reviewRepository.save(existing);
    }

    // Delete
    public void handleDelete(Long id)
            throws InvalidException {
        Review existing = reviewRepository.findById(id)
                .orElseThrow(() -> new InvalidException(
                        "Không  tìm thấy Review với id = " + id));

        // Gỡ Review khỏi danh sách trong Tour
        Tour tour = existing.getTour();
        if (tour != null && tour.getReviews() != null) {
            tour.getReviews().remove(existing);
        }

        reviewRepository.delete(existing);
    }
}