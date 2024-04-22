package com.demo.filestorage.services.impl.s3;

import com.demo.filestorage.configs.S3Config;
import com.demo.filestorage.models.exceptions.FileNotFoundException;
import com.demo.filestorage.models.exceptions.UserNotFoundException;
import com.demo.filestorage.services.IFileSearchService;
import com.demo.filestorage.services.impl.AbstractFileSearchService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class S3FileSearchService extends AbstractFileSearchService {

    private static final Logger log = LogManager.getLogger(S3FileSearchService.class);

    @Override
    @Cacheable(value = "fileCache", key = "#userName + '_' + #fileName")
    public ByteArrayResource searchFile(String userName, String fileName) throws Exception{
        Region awsRegion = Region.of(S3Config.getBucketRegion());
        S3Client s3 = S3Client.builder().region(awsRegion).build();
        ListObjectsV2Response res = s3.listObjectsV2(ListObjectsV2Request.builder().bucket(S3Config.getBucketName()).build());

        Boolean userExists = getUserExists(s3, userName);

        if(Boolean.FALSE.equals(userExists)){
            throw new UserNotFoundException("User Not Found");
        }

        ByteArrayResource file = getObjectFromS3(s3, userName + "/" + fileName);

        if(Objects.isNull(file)){
            throw new FileNotFoundException("File Not Found");
        }

        return file;
    }

    private boolean getUserExists(S3Client s3, String userName){
        try {
            ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(S3Config.getBucketName()).prefix(userName).build();
            ListObjectsV2Response response = s3.listObjectsV2(request);

            if(response.hasContents()){
                Optional<S3Object> user = response.contents().stream().filter(s3Object ->  s3Object.key().split("/")[0].equals(userName)).findFirst();
                return user.isPresent();
            }
        } catch (S3Exception e){
            log.error("Error getting Object From S3", e);
        }
        return false;
    }

    private static ByteArrayResource getObjectFromS3(S3Client s3, String objectKey){
        try {
            GetObjectRequest req = GetObjectRequest.builder().bucket(S3Config.getBucketName()).key(objectKey).build();

            ResponseBytes<GetObjectResponse> s3objectResponse = s3.getObjectAsBytes(req);

            ByteArrayResource resource = new ByteArrayResource(s3objectResponse.asByteArray());

            return resource;
        } catch (NoSuchKeyException e){
            log.error("Error getting Object From S3", e);
            return null;
        }
    }

}
