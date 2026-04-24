package com.J2EE.TourManagement.Model;

import com.J2EE.TourManagement.Util.constan.EnumTourDetailStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "tour_details")
@Getter
@Setter
public class TourDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id")
    @JsonBackReference(value = "detail-tour")
    private Tour tour;

    @Column(name = "tour_id", insertable = false, updatable = false)
    private Long tourId;

    @Column(name = "startLocation")
    @NotBlank(message = "Điểm khởi hành không được để trống")
    private String startLocation;

    @Column(name = "startDay")
    @NotNull(message = "Ngày bắt đầu không được để trống")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+7")
    private LocalDate startDay;

    @Column(name = "endDay")
    @NotNull(message = "Ngày kết thúc không được để trống")
    @FutureOrPresent(message =
            "Ngày kết thúc phải là hiện tại hoặc trong tương lai")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+7")
    private LocalDate endDay;

    @Column(name = "capacity")
    @NotNull(message = "Tổng số chỗ không được để trống")
    @Min(value = 0, message = "Tổng số chỗ không được âm")
    private Integer capacity;

    @Column(name = "remainingSeats")
    @NotNull(message = "Số chỗ còn lại không được để trống")
    @Min(value = 0, message = "Số chỗ còn lại không được âm")
    private Integer remainingSeats;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EnumTourDetailStatus status;

    @Column(updatable = false, name = "createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+7")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+7")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "tourDetail", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference(value = "detail-price")
    private List<TourPrice> tourPrices;

    @OneToMany(mappedBy = "tourDetail", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference(value = "detail-itinerary")
    private List<TourItinerary> itineraries;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = (this.status == null) ? EnumTourDetailStatus.DRAFT : this.status;
        if (this.remainingSeats == null && this.capacity != null) {
            this.remainingSeats = this.capacity;
        }
    }
}
