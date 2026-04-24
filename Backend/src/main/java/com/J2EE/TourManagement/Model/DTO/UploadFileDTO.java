package com.J2EE.TourManagement.Model.DTO;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UploadFileDTO {
    private String fileName;
    private Instant uploadAt;
}
