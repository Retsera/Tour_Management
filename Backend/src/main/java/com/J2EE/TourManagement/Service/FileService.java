package com.J2EE.TourManagement.Service;

import com.amazonaws.services.s3.AmazonS3;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {
  @Value("${mt.upload-file.base-uri}") private String basePath;

  private final AmazonS3 s3Client;
  @Value("${aws.s3.bucket-name}") private String bucketName;

  public FileService(AmazonS3 s3Client){
    this.s3Client = s3Client;
  }

  public void createDirectory(String folder) throws URISyntaxException {
    URI uri = new URI(folder);
    Path path = Paths.get(uri);
    File tmpDir = new File(path.toString());
    if (!tmpDir.isDirectory()) {
      try {
        Files.createDirectory(tmpDir.toPath());
        System.out.println(">>> CREATE NEW DIRECTORY SUCCESSFUL, PATH = " +
                           folder);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      System.out.println(">>> SKIP MAKING DIRECTORY, ALREADY EXISTS");
    }
  }

  public String store(MultipartFile file, String folder)
      throws URISyntaxException, IOException {
    // create unique filename
    String finalName =
        System.currentTimeMillis() + "-" + file.getOriginalFilename();

    URI uri = new URI(basePath + folder + "/" + finalName);
    Path path = Paths.get(uri);
    try (InputStream inputStream = file.getInputStream()) {
      Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
    }
    return finalName;
  }

  // // Xoá file theo folder + fileName
  // public boolean deleteFile(String folder, String fileName)
  //     throws IOException, URISyntaxException {
  //   Path filePath =
  //       Paths.get(new URI(basePath)).resolve(folder).resolve(fileName);
  //   System.out.println("Xoá file tại: " + filePath.toAbsolutePath());

  //   if (Files.exists(filePath)) {
  //     return Files.deleteIfExists(filePath);
  //   } else {
  //     System.out.println("File không tồn tại");
  //     return false;
  //   }
  // }

  // Xoá file theo URL upload
  public boolean deleteFileByUrl(String fileUrl) throws IOException {
    // 1. Lấy key từ URL
    String key = extractKeyFromUrl(fileUrl);

    // 2. Kiểm tra object tồn tại
    if (!s3Client.doesObjectExist(bucketName, key)) {
      return false;
    }

    // 3. Xoá object
    s3Client.deleteObject(bucketName, key);

    return true;
  }

  private String extractKeyFromUrl(String fileUrl) {
    // URL: https://my-bucket.s3.region.amazonaws.com/folder/file.png
    // => key = folder/file.png
    return fileUrl.substring(fileUrl.indexOf(".com/") + 5);
  }
}
