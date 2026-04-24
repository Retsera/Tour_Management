package com.J2EE.TourManagement.Controller;

import com.J2EE.TourManagement.Model.DTO.VoucherDTO;
import com.J2EE.TourManagement.Model.DTO.VoucherRequestDTO;
import com.J2EE.TourManagement.Model.User;
import com.J2EE.TourManagement.Model.UserVoucher;
import com.J2EE.TourManagement.Model.Voucher;
import com.J2EE.TourManagement.Service.UserSer;
import com.J2EE.TourManagement.Service.VoucherService;
import com.J2EE.TourManagement.Util.annotation.ApiMessage;
import com.J2EE.TourManagement.Util.error.InvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vouchers")
@RequiredArgsConstructor
public class VoucherController {

  private final VoucherService voucherService;
  private final UserSer userSer;

  // CREATE --------------------------------------------------------------------
  @PostMapping
  @ApiMessage("Tạo Voucher thành công.")
  public ResponseEntity<Voucher>
  createVoucher(@RequestBody @Valid VoucherDTO dto) throws InvalidException {
    if (this.voucherService.existsByCode(dto.getCode())) {
      throw new InvalidException("Mã Voucher đã tồn tại");
    }
    Voucher voucher = voucherService.create(dto);
    return ResponseEntity.ok(voucher);
  }

  // READ ALL ------------------------------------------------------------------
  @GetMapping
  public ResponseEntity<?> getAllVouchers(@Filter Specification<Voucher> spec,
                                          Pageable pageable) {
    return ResponseEntity.ok(voucherService.getAll(spec, pageable));
  }

  // READ ONE ------------------------------------------------------------------
  @GetMapping("/{id}")
  public ResponseEntity<Voucher> getVoucher(@PathVariable("id") long id)
      throws InvalidException {
    Voucher voucher = voucherService.getById(id);
    if (voucher == null) {
      throw new InvalidException("Voucher không tồn tại.");
    }
    return ResponseEntity.ok(voucher);
  }

  // UPDATE --------------------------------------------------------------------
  @PutMapping("/{id}")
  public ResponseEntity<Voucher>
  updateVoucher(@PathVariable("id") Long id, @RequestBody @Valid VoucherDTO dto)
      throws InvalidException {

    Voucher voucher = voucherService.getById(id);
    if (voucher == null) {
      throw new InvalidException("Voucher không tồn tại.");
    }
    return ResponseEntity.ok(voucherService.update(id, dto));
  }
  // toggle status voucher
  @PutMapping("/{id}/status")
  public ResponseEntity<Voucher> toggleStatus(@PathVariable("id") long id)
      throws InvalidException {
    Voucher voucher = voucherService.getById(id);
    if (voucher == null) {
      throw new InvalidException("Voucher không tồn tại.");
    }
    Voucher updatedVoucher = voucherService.updateStatus(id);
    return ResponseEntity.ok(updatedVoucher);
  }

  @PostMapping("/assign")
  public ResponseEntity<UserVoucher>
  assignVoucher(@RequestBody VoucherRequestDTO request)
      throws InvalidException {
    User user = userSer.getUserById(request.getUserId());
    if (user == null) {
      throw new InvalidException("người dùng không tồn tại.");
    }
    Voucher voucher = voucherService.getByCode(request.getVoucherCode());
    if (voucher == null) {
      throw new InvalidException("voucher không tồn tại.");
    }
    UserVoucher uv = voucherService.assignVoucherToUser(user, voucher);
    return ResponseEntity.ok(uv);
  }

  //   @PostMapping("/use")
  //   public ResponseEntity<UserVoucher>
  //   useVoucher(@RequestBody VoucherRequestDTO request) {
  //     try {
  //       UserVoucher uv = voucherService.useVoucher(request.getUserId(),
  //                                                  request.getVoucherCode());
  //       return ResponseEntity.ok(uv);
  //     } catch (RuntimeException e) {
  //       return ResponseEntity.badRequest().build();
  //     }
  //   }
}
