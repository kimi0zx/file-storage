package com.demo.filestorage.api;

import com.demo.filestorage.models.exceptions.FileNotFoundException;
import com.demo.filestorage.models.exceptions.UserNotFoundException;
import com.demo.filestorage.services.impl.s3.S3FileSearchService;
import com.demo.filestorage.services.impl.s3.S3FileUploadService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class File {

    private static final Logger log = LogManager.getLogger(File.class);

    @Autowired
    S3FileSearchService fss;

    @Autowired
    S3FileUploadService fus;

    @GetMapping("/search")
    public ResponseEntity<ByteArrayResource> searchFile(@RequestParam String userName, @RequestParam String fileName){
        try {
            ByteArrayResource resource = fss.searchFile(userName, fileName);

            return ResponseEntity.status(HttpStatus.OK)
                    .headers(createFileDownloadHeaders(fileName))
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ByteArrayResource("User not found".getBytes()));
        } catch (FileNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ByteArrayResource("File not found".getBytes()));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ByteArrayResource(("Error Searching File" + e.getMessage()).getBytes()));
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam String userName, @RequestParam("file") MultipartFile file){
        try {
            boolean successful = fus.uploadFile(userName, file);

            if(successful){
                return ResponseEntity.status(HttpStatus.OK).body("File uploaded successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload Failed");
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload Failed");
        }
    }

    private HttpHeaders createFileDownloadHeaders(String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return headers;
    }
}
