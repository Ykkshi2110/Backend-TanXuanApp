package com.peter.tanxuannewapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;

@Service
public class FileService {
    private final Logger logger = LoggerFactory.getLogger(FileService.class);
    private final Random rand = new Random();

    @Value("${peterBui.upload-file.base-uri}")
    private String baseURI;

    public void createDirectory(String folder) throws URISyntaxException {
        // convert to Path
        URI uri = new URI(folder);
        Path path = Paths.get(uri);

        File uploadFolder = path.toFile();
        if (!uploadFolder.isDirectory()) {
            try {
                Files.createDirectory(uploadFolder.toPath());
                logger.info(">>>> DIRECTORY CREATED, Path = {}", uploadFolder.getAbsolutePath());
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        } else {
            logger.info(">>>> SKIP MAKING DIRECTORY, ALREADY EXISTS: {}", uploadFolder.getAbsolutePath());
        }
    }

    public String generateUniqueFileName(String fileName) {

        int randomNum = this.rand.nextInt(10000);
        return randomNum + "_" + fileName;
    }

    public String handleUploadFile(MultipartFile file, String folder) throws URISyntaxException, IOException {
        // create unique fileName
        String uniqueFileName = generateUniqueFileName(file.getOriginalFilename());

        URI uri = new URI(baseURI).resolve(folder + "/").resolve(uniqueFileName);
        Path destinationFile = Paths.get(uri);

        try (InputStream inputStream = file.getInputStream()){
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        }

        return uniqueFileName;
    }
}
