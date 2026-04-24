package com.J2EE.TourManagement.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tours")
public class Tour {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

  @NotBlank(message = "Tiêu đề không được để trống")
  @Size(max = 255, message = "Tiêu đề không được vượt quá 255 ký tự")
  private String title;

  @Column(length = 500)
  @Size(max = 500, message = "Mô tả ngắn không được vượt quá 500 ký tự")
  private String shortDesc;

  @Lob @Column(columnDefinition = "LONGTEXT") private String longDesc;

  @Pattern(regexp = "\\d+\\s*ngày\\s*\\d+\\s*đêm",
           message = "Thời lượng phải đúng định dạng: 'X ngày Y đêm'")
  private String duration;

  @NotBlank(message = "Điểm đến không được để trống")
  @Size(max = 255, message = "Điểm đến không được vượt quá 255 ký tự")
  private String location;

  private String imageUrl;

  @NotBlank(message = "Trạng thái không được để trống")
  @Pattern(regexp = "ACTIVE|INACTIVE|DRAFT",
           message = "Trạng thái phải là ACTIVE, INACTIVE hoặc DRAFT")
  private String status;

  @Column(updatable = false)
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt;
  
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") private LocalDateTime updatedAt;
  @Column(name = "rating") private Double rating;

  private String createdBy;
  private String updatedBy;

  @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JsonManagedReference(value = "detail-tour")
  private List<TourDetail> tourDetails;

  // Quan hệ 1 Tour có nhiều Review
  @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @JsonManagedReference(value = "detail-review")
  private List<Review> reviews;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    if (this.status == null) {
      this.status = "DRAFT";
    }
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
}
