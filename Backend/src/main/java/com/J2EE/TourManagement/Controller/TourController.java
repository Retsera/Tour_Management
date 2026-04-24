package com.J2EE.TourManagement.Controller;

import com.J2EE.TourManagement.Mapper.TourMapper;
import com.J2EE.TourManagement.Model.DTO.ResultPaginationDTO;
import com.J2EE.TourManagement.Model.DTO.Tour.TourDTO;
import com.J2EE.TourManagement.Model.DTO.Tour.TourUpdateDTO;
import com.J2EE.TourManagement.Model.DTO.UploadFileDTO;
import com.J2EE.TourManagement.Model.Tour;
import com.J2EE.TourManagement.Service.FileService;
import com.J2EE.TourManagement.Service.TourService;
import com.J2EE.TourManagement.Util.annotation.ApiMessage;
import com.J2EE.TourManagement.Util.error.InvalidException;
import com.J2EE.TourManagement.Util.error.StorageException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/tours")
public class TourController {
  private final TourService tourService;
  private final TourMapper tourMapper;
  private final FileService fileService;
  private final AmazonS3 s3Client;
  @Value("${aws.s3.bucket-name}") private String bucketName;

  @Value("${mt.upload-file.base-uri}") private String basePath;

  public TourController(TourService tourService, FileService fileService,
                        TourMapper tourMapper, AmazonS3 s3Client) {
    this.tourService = tourService;
    this.fileService = fileService;
    this.tourMapper = tourMapper;
    this.s3Client = s3Client;
  }

  // @PostMapping("/file")
  // @ApiMessage("uploadFile")
  // public ResponseEntity<UploadFileDTO>
  // postMethodName(@RequestParam(name = "file", required = false)
  //                MultipartFile file, @RequestParam("folder") String folder)
  //     throws URISyntaxException, IOException, StorageException {
  //   // valid
  //   if (file == null || file.isEmpty()) {
  //     throw new StorageException("file is empty.");
  //   }
  //   String fileName = file.getOriginalFilename();
  //   List<String> allowedExtensions =
  //       Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");

  //   boolean idValid = allowedExtensions.stream().anyMatch(
  //       item -> fileName.toLowerCase().endsWith(item));

  //   if (!idValid) {
  //     throw new StorageException("file không hợp lệ. chỉ những file: " +
  //                                allowedExtensions.toString());
  //   }
  //   // create folder if not exits
  //   this.fileService.createDirectory(basePath + folder);
  //   // store file
  //   String uploadFile = "http://localhost:8080/storage/" + folder + "/" +
  //                       this.fileService.store(file, folder);
  //   UploadFileDTO uploadFileDTO = new UploadFileDTO(uploadFile,
  //   Instant.now()); return ResponseEntity.ok().body(uploadFileDTO);
  // }

  @PostMapping("/upload")
  public ResponseEntity<UploadFileDTO>
  uploadFile(@RequestParam(name = "file", required = false) MultipartFile file,
             @RequestParam("folder") String folder)
      throws URISyntaxException, IOException, InvalidException {

    if (file == null || file.isEmpty()) {
      throw new InvalidException("file is empty.");
    }

    String fileName = file.getOriginalFilename();
    List<String> allowedExtensions =
        Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");

    boolean isValid = allowedExtensions.stream().anyMatch(
        ext -> fileName.toLowerCase().endsWith(ext));

    if (!isValid) {
      throw new InvalidException("File không hợp lệ. Chỉ những file: " +
                                 allowedExtensions);
    }
    // Đặt đường dẫn object trong bucket
    String key = folder + "/" + fileName;

    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(file.getSize());
    metadata.setContentType(file.getContentType());

    // ⚡ Upload object với quyền PublicRead
    PutObjectRequest putObjectRequest =
        new PutObjectRequest(bucketName, key, file.getInputStream(), metadata)
            .withCannedAcl(CannedAccessControlList.PublicRead);

    s3Client.putObject(putObjectRequest);

    // Lấy URL public
    String fileUrl = s3Client.getUrl(bucketName, key).toString();

    UploadFileDTO uploadFileDTO = new UploadFileDTO(fileUrl, Instant.now());
    return ResponseEntity.ok().body(uploadFileDTO);
  }

  @DeleteMapping("/file")
  public ResponseEntity<String> deleteFile(@RequestParam("url") String fileUrl)
      throws URISyntaxException {
    try {
      boolean deleted = fileService.deleteFileByUrl(fileUrl);
      if (deleted) {
        return ResponseEntity.ok("Xoá file thành công");
      } else {
        return ResponseEntity.status(404).body("File không tồn tại");
      }
    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body("Lỗi: " + e.getMessage());
    }
  }

  // Create
  @PostMapping
  @ApiMessage("Thêm tour thành công.")
  public ResponseEntity<TourDTO> postNewTour(@Valid @RequestBody Tour tour) {
    Tour reponse = tourService.handleSave(tour);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(tourMapper.toDTO(reponse));
  }

  // Read all
  @GetMapping
  public ResponseEntity<ResultPaginationDTO>
  fetchAllTour(@Filter Specification<Tour> spec, Pageable pageable) {
    return ResponseEntity.ok(tourService.handleGetAll(spec, pageable));
  }

  // Read by id
  @GetMapping("/{id}")
  public ResponseEntity<?> getTourById(@PathVariable("id") long id)
      throws InvalidException {

    Tour reponse = tourService.handleGetById(id);

    return ResponseEntity.ok(tourMapper.toDTO(reponse));
  }

  // Update
  @PutMapping("/{id}")
  @ApiMessage("cập nhật tour thành công.")
  public ResponseEntity<TourDTO>
  updateTour(@PathVariable Long id, @Valid @RequestBody TourUpdateDTO dto)
      throws InvalidException {
    Tour reponse = tourService.handleUpdate(id, dto);

    return ResponseEntity.ok(tourMapper.toDTO(reponse));
  }

  
  @GetMapping("/search")
  public ResponseEntity<?> searchTours(
      @RequestParam(name = "location", required = false) String location,
      @RequestParam(name = "startLocation",
                    required = false) String startLocation,
      @RequestParam(name = "remainingSeats",
                    required = false) Integer remainingSeats) {
    return ResponseEntity.ok(
        tourService.searchTours(location, startLocation, remainingSeats));
  }
}
