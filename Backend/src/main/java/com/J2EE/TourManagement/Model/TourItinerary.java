package com.J2EE.TourManagement.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "tour_itineraries")
@Getter
@Setter
public class TourItinerary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tour_detail_id")
    @JsonBackReference(value = "detail-itinerary")
    private TourDetail tourDetail;

    @NotBlank(message = "Tiêu đề lịch trình không được để trống")
    @Size(max = 255, message = "Tiêu đề lịch trình không được vượt quá 255 ký tự")
    private String title;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    @NotBlank(message = "Nội dung lịch trình không được để trống")
    private String content;

    @Column(updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+7")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+7")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
