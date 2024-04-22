package com.demo.filestorage.services.impl.s3;

import com.demo.filestorage.configs.S3Config;
import com.demo.filestorage.services.IFileUploadService;
import com.demo.filestorage.services.impl.AbstractFileUploadService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;

@Service
public class S3FileUploadService extends AbstractFileUploadService {

    private static final Logger log = LogManager.getLogger(S3FileUploadService.class);

    public boolean uploadFile(String userName, MultipartFile file){
        Region awsRegion = Region.of(S3Config.getBucketRegion());
        S3Client s3 = S3Client.builder().region(awsRegion).build();

        String objectkey = userName + "/" + file.getOriginalFilename();

        try {
            putObjectToS3(s3, file, objectkey);
            return true;
        } catch (Exception e){
            log.error("Error uploading File", e);
        }
        return false;
    }

    private static void putObjectToS3(S3Client s3, MultipartFile file, String objectKey) throws IOException {
        PutObjectRequest request = PutObjectRequest.builder().bucket(S3Config.getBucketName()).key(objectKey).build();
        s3.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    }

}
