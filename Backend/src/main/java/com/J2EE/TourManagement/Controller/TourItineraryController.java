package com.J2EE.TourManagement.Controller;

import com.J2EE.TourManagement.Mapper.TourItineraryMapper;
import com.J2EE.TourManagement.Model.DTO.ResultPaginationDTO;
import com.J2EE.TourManagement.Model.DTO.TourItinerary.TourItineraryCreateDTO;
import com.J2EE.TourManagement.Model.DTO.TourItinerary.TourItineraryDTO;
import com.J2EE.TourManagement.Model.DTO.TourItinerary.TourItineraryUpdateDTO;
import com.J2EE.TourManagement.Model.TourItinerary;
import com.J2EE.TourManagement.Service.TourItineraryService;
import com.J2EE.TourManagement.Util.annotation.ApiMessage;
import com.J2EE.TourManagement.Util.error.InvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tour_details")
public class TourItineraryController {

    private final TourItineraryService tourItineraryService;
    private final TourItineraryMapper tourItineraryMapper;

    public TourItineraryController(TourItineraryService tourItineraryService, TourItineraryMapper tourItineraryMapper) {
        this.tourItineraryService = tourItineraryService;
        this.tourItineraryMapper = tourItineraryMapper;
    }

    //Create
    @PostMapping("/itinerary")
    @ApiMessage("Thêm lộ trình tour thành công!")
    public ResponseEntity<TourItineraryDTO> postNewTourItinerary(@Valid @RequestBody TourItineraryCreateDTO dto) throws InvalidException {
        TourItinerary response = tourItineraryService.handleSave(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(tourItineraryMapper.toResponseDTO(response));
    }

    //Read all
    @GetMapping("/itinerary")
    public ResponseEntity<ResultPaginationDTO> fetchAllTourItinerary(@Filter Specification<TourItinerary> spec, Pageable pageable) {
        return ResponseEntity.ok(tourItineraryService.handleGetAll(spec, pageable));
    }

    //Update
    @PutMapping("/itinerary/{id}")
    @ApiMessage("Cập nhật lộ trình tour thành công.")
    public ResponseEntity<TourItineraryDTO> updateItinerary(@PathVariable Long id, @RequestBody TourItineraryUpdateDTO itineraryUpdateDTO) throws InvalidException {
        TourItinerary updated = tourItineraryService.handleUpdate(id, itineraryUpdateDTO);
        return ResponseEntity.ok(tourItineraryMapper.toResponseDTO(updated));
    }

    // Delete
    @ApiMessage("Xóa lộ trình tour thành công!")
    @DeleteMapping("/itinerary/{id}")
    public ResponseEntity<List<TourItinerary>> delete(@PathVariable Long id)
            throws InvalidException {
        tourItineraryService.handleDelete(id);
        return ResponseEntity.ok(List.of());
    }
}
