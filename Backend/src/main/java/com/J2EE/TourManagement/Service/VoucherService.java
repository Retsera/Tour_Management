package com.J2EE.TourManagement.Service;

import com.J2EE.TourManagement.Model.Booking;
import com.J2EE.TourManagement.Model.DTO.BookingResponseDTO;
import com.J2EE.TourManagement.Model.DTO.Meta;
import com.J2EE.TourManagement.Model.DTO.ResultPaginationDTO;
import com.J2EE.TourManagement.Model.DTO.VoucherDTO;
import com.J2EE.TourManagement.Model.User;
import com.J2EE.TourManagement.Model.UserVoucher;
import com.J2EE.TourManagement.Model.Voucher;
import com.J2EE.TourManagement.Model.Voucher.VoucherStatus;
import com.J2EE.TourManagement.Repository.UserVoucherRepo;
import com.J2EE.TourManagement.Repository.VoucherRepo;
import java.lang.annotation.ElementType;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VoucherService {

  private final UserVoucherRepo userVoucherRepo;
  private final VoucherRepo voucherRepo;

  public Voucher create(VoucherDTO dto) {
    Voucher voucher = mapDtoToEntity(dto);
    voucher.setStatus(VoucherStatus.ACTIVE);
    return voucherRepo.save(voucher);
  }

  public boolean existsByCode(String code) {
    return this.voucherRepo.existsByCode(code);
  }

  // READ ALL ---------------------------------------------------
  public ResultPaginationDTO getAll(Specification<Voucher> spec,
                                    Pageable pageable) {
    Page<Voucher> pageVoucher = voucherRepo.findAll(spec, pageable);
    Meta meta = new Meta();
    meta.setPageCurrent(pageable.getPageNumber() + 1);
    meta.setPageSize(pageable.getPageSize());
    meta.setPages(pageVoucher.getTotalPages());
    meta.setTotal(pageVoucher.getTotalElements());

    ResultPaginationDTO result = new ResultPaginationDTO();
    result.setMeta(meta);

    List<Voucher> vouchers =
        pageVoucher.getContent()
            .stream()
            .map(item -> {
              Voucher dto = new Voucher();
              dto.setId(item.getId());
              dto.setCode(item.getCode());
              dto.setDescription(item.getDescription());
              dto.setDiscountType(item.getDiscountType());
              dto.setDiscountValue(item.getDiscountValue());
              dto.setMinOrder(item.getMinOrder());
              dto.setMaxDiscount(item.getMaxDiscount());
              dto.setQuantity(item.getQuantity());
              dto.setStartDate(item.getStartDate());
              dto.setEndDate(item.getEndDate());
              dto.setStatus(item.getStatus());
              return dto;
            })
            .collect(Collectors.toList());

    result.setResult(vouchers);
    return result;
  }

  // READ ONE ---------------------------------------------------
  public Voucher getById(long id) {
    Voucher voucher = this.voucherRepo.findById(id).isPresent()
                          ? this.voucherRepo.findById(id).get()
                          : null;
    return voucher;
  }

  //

  public Voucher getByCode(String code){
    Voucher voucher = this.voucherRepo.findByCode(code).isPresent() ? this.voucherRepo.findByCode(code).get() : null;
    return voucher;
  }

  // UPDATE -----------------------------------------------------
  public Voucher update(Long id, VoucherDTO dto) {
    Voucher voucher = getById(id);

    voucher.setDescription(dto.getDescription());
    voucher.setDiscountType(dto.getDiscountType());
    voucher.setDiscountValue(dto.getDiscountValue());
    voucher.setMinOrder(dto.getMinOrder());
    voucher.setMaxDiscount(dto.getMaxDiscount());
    voucher.setQuantity(dto.getQuantity());
    voucher.setStartDate(dto.getStartDate());
    voucher.setEndDate(dto.getEndDate());
    // code không cho sửa
    return voucherRepo.save(voucher);
  }

  // Update Status Voucher (toggle)
  public Voucher updateStatus(long id) {
    Voucher voucher = getById(id);

    if (voucher.getStatus() == VoucherStatus.ACTIVE) {
      voucher.setStatus(VoucherStatus.INACTIVE);
    } else {
      voucher.setStatus(VoucherStatus.ACTIVE);
    }

    return voucherRepo.save(voucher);
  }

  // SUPPORT: mapper --------------------------------------------
  private Voucher mapDtoToEntity(VoucherDTO dto) {
    Voucher voucher = new Voucher();
    voucher.setCode(dto.getCode());
    voucher.setDescription(dto.getDescription());
    voucher.setDiscountType(dto.getDiscountType());
    voucher.setDiscountValue(dto.getDiscountValue());
    voucher.setMinOrder(dto.getMinOrder());
    voucher.setMaxDiscount(dto.getMaxDiscount());
    voucher.setQuantity(dto.getQuantity());
    voucher.setStartDate(dto.getStartDate());
    voucher.setEndDate(dto.getEndDate());

    return voucher;
  }

  public UserVoucher assignVoucherToUser(User user, Voucher voucher) {
    // Kiểm tra user đã có voucher chưa
    boolean exists = userVoucherRepo.existsByUserIdAndVoucherId(
        user.getId(), voucher.getId());
    if (exists)
      throw new RuntimeException("User đã được cấp voucher này");

    // Tạo UserVoucher mới
    UserVoucher uv = new UserVoucher();
    uv.setUser(user);
    uv.setVoucher(voucher);
    uv.setIsUsed(false);
    uv.setUniqueCode(voucher.getCode() + "-" + user.getId() + "-" +
                     System.currentTimeMillis());

    // Giảm số lượng voucher nếu giới hạn
    if (voucher.getQuantity() != null && voucher.getQuantity() > 0) {
      voucher.setQuantity(voucher.getQuantity() - 1);
      voucherRepo.save(voucher);
    }

    return userVoucherRepo.save(uv);
  }

  public void useVoucher(Long userId, String voucherCode) {
    UserVoucher uv =
        userVoucherRepo
            .findByUser_IdAndVoucher_IdAndIsUsedFalse(
                userId,
                voucherRepo.findByCode(voucherCode)
                    .orElseThrow(
                        () -> new RuntimeException("Voucher không tồn tại"))
                    .getId())
            .orElseThrow(()
                             -> new RuntimeException(
                                 "Voucher chưa được cấp hoặc đã sử dụng"));

    Voucher voucher = uv.getVoucher();

    // Kiểm tra thời gian hiệu lực
    LocalDateTime now = LocalDateTime.now();
    if (voucher.getStartDate().isAfter(now) ||
        voucher.getEndDate().isBefore(now))
      throw new RuntimeException("Voucher chưa đến hạn hoặc đã hết hạn");

    uv.setIsUsed(true);
    uv.setUsedDate(Instant.now());
    userVoucherRepo.save(uv);
  }
}
