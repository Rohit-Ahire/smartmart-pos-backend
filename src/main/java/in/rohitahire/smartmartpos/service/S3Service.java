package in.rohitahire.smartmartpos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.bucket-name}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    public String uploadFile(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        s3Client.putObject(
                putObjectRequest,
                RequestBody.fromBytes(file.getBytes())
        );

        return String.format(
                "https://%s.s3.%s.amazonaws.com/%s",
                bucketName,
                region,
                fileName
        );
    }

    public void deleteFile(String imageUrl) {

        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        String prefix = String.format(
                "https://%s.s3.%s.amazonaws.com/",
                bucketName,
                region
        );

        if (!imageUrl.startsWith(prefix)) {
            return;
        }

        String key = imageUrl.replace(prefix, "");

        s3Client.deleteObject(builder -> builder
                .bucket(bucketName)
                .key(key)
        );
    }
}