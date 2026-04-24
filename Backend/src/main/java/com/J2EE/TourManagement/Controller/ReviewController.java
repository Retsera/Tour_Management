package com.J2EE.TourManagement.Controller;


import com.J2EE.TourManagement.Mapper.ReviewMapper;
import com.J2EE.TourManagement.Model.DTO.Review.*;
import com.J2EE.TourManagement.Model.DTO.TourDetail.TourDetailDTO;
import com.J2EE.TourManagement.Model.Review;
import com.J2EE.TourManagement.Service.ReviewService;
import com.J2EE.TourManagement.Util.annotation.ApiMessage;
import com.J2EE.TourManagement.Util.error.InvalidException;
import jakarta.validation.Path;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/tours")
public class ReviewController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    public ReviewController(ReviewService reviewService, ReviewMapper reviewMapper) {
        this.reviewService = reviewService;
        this.reviewMapper = reviewMapper;
    }

    //Create
    @ApiMessage("Thêm Review thành công!")
    @PostMapping("/reviews")
    public ResponseEntity<ReviewDTO> create(@Valid @RequestBody ReviewCreateDTO dto)
            throws InvalidException {
        Review reponse = reviewService.handleSave(dto);
        messagingTemplate.convertAndSend(
                "/topic/reviews/" + reponse.getTour().getId(),
                reponse
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewMapper.toResponseDTO(reponse));
    }

    //Read by tourDetail id
    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ReviewDTO>> read(@PathVariable Long id)
            throws InvalidException {
        List<Review> response = reviewService.handleGetAll(id);

        return ResponseEntity.ok(reviewMapper.toResponseDTOList(response));
    }

    //Update
    @ApiMessage("Sửa Review thành công!")
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewDTO> update(@PathVariable Long reviewId, @Valid @RequestBody ReviewUpdateDTO dto)
            throws InvalidException {
        Review reponse = reviewService.handleUpdate(reviewId, dto);

        return ResponseEntity.ok(reviewMapper.toResponseDTO(reponse));
    }

    // Delete
    @ApiMessage("Xóa review thành công!")
    @DeleteMapping("reviews/{id}")
    public ResponseEntity<List<Review>> delete(@PathVariable Long id)
            throws InvalidException {
        reviewService.handleDelete(id);
        return ResponseEntity.ok(List.of());
    }
}
