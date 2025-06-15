package com.peter.tanxuannewapp.controller;

import com.peter.tanxuannewapp.domain.resposne.file.ResUploadFileDTO;
import com.peter.tanxuannewapp.exception.StorageException;
import com.peter.tanxuannewapp.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @Value("${peterBui.upload-file.base-uri}")
    private String baseURI;

    @PostMapping("/files")
    public ResponseEntity<ResUploadFileDTO> uploadFile(@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam("folder") String folder) throws URISyntaxException, IOException {
        // check Validate
        if(file == null || file.isEmpty()) {
            throw new StorageException("File is empty! Please upload file!");
        }


        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");
        boolean isValidExtension = allowedExtensions.stream().anyMatch(extension -> Objects
                .requireNonNull(file.getOriginalFilename())
                .toLowerCase().endsWith(extension));
        if(!isValidExtension) {
            throw new StorageException("File is not supported!");
        }

        // create folder
        this.fileService.createDirectory(baseURI + folder);
        // store file
        ResUploadFileDTO uploadFileDTO = new ResUploadFileDTO();
        uploadFileDTO.setFileName(this.fileService.handleUploadFile(file, folder));
        uploadFileDTO.setUploadedAt(Instant.now());
        return ResponseEntity.ok().body(uploadFileDTO);
    }
}
