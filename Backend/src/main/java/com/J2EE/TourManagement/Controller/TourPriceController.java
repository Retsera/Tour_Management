package com.J2EE.TourManagement.Controller;

import com.J2EE.TourManagement.Mapper.TourPriceMapper;
import com.J2EE.TourManagement.Model.DTO.TourPrice.TourPriceCreateDTO;
import com.J2EE.TourManagement.Model.DTO.TourPrice.TourPriceDTO;
import com.J2EE.TourManagement.Model.DTO.TourPrice.TourPriceUpdateDTO;
import com.J2EE.TourManagement.Model.TourPrice;
import com.J2EE.TourManagement.Service.TourPriceService;
import com.J2EE.TourManagement.Util.annotation.ApiMessage;
import com.J2EE.TourManagement.Util.error.InvalidException;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/tour_details")
public class TourPriceController {

    private final TourPriceService tourPriceService;
    private final TourPriceMapper tourPriceMapper;

    public TourPriceController(TourPriceService tourPriceService, TourPriceMapper tourPriceMapper) {
        this.tourPriceService = tourPriceService;
        this.tourPriceMapper = tourPriceMapper;
    }

    // Read all by tour detailId
    @GetMapping("/{id}/prices")
    public ResponseEntity<List<TourPriceDTO>> fetchTourPriceByTourId(@PathVariable("id") Long id)
            throws InvalidException {
        return ResponseEntity.ok(tourPriceService.handleGetAll(id));
    }

    //Create
    @ApiMessage("Thêm tour price thành công!")
    @PostMapping("/prices")
    public ResponseEntity<TourPriceDTO> create(@Valid @RequestBody TourPriceCreateDTO price)
            throws InvalidException {
        TourPrice reponse = tourPriceService.handleSave(price);

        return ResponseEntity.status(HttpStatus.CREATED).body(tourPriceMapper.toDTO(reponse));
    }

    //Update
    @ApiMessage("Sửa tour price thành công!")
    @PutMapping("/prices/{id}")
    public ResponseEntity<TourPriceDTO> update(@PathVariable Long id, @Valid @RequestBody TourPriceUpdateDTO dto)
            throws InvalidException {
        TourPrice reponse = tourPriceService.handleUpdate(id, dto);

        return ResponseEntity.ok(tourPriceMapper.toDTO(reponse));
    }

    // Delete
    @ApiMessage("Xóa tour Price thành công!")
    @DeleteMapping("/prices/{id}")
    public ResponseEntity<List<TourPrice>> delete(@PathVariable Long id)
            throws InvalidException {
        tourPriceService.handleDelete(id);
        return ResponseEntity.ok(List.of());
    }
}
