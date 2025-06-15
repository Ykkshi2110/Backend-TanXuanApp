package com.peter.tanxuannewapp.domain.resposne.file;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResUploadFileDTO {
    private String fileName;
    private Instant uploadedAt;
}
