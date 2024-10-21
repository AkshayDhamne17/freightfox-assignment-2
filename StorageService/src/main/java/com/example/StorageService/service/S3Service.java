package com.example.StorageService.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
public class S3Service {
	private final S3Client s3Client;
    private final String bucketName = "your-bucket-name"; // Replace with your bucket name

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public List<String> searchFiles(String userName, String fileName) {
        String userFolder = userName + "/";
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(userFolder)
                .build();

        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

        return listResponse.contents().stream()
                .filter(s3Object -> s3Object.key().contains(fileName))
                .map(s3Object -> s3Object.key())
                .collect(Collectors.toList());
    }

    public String uploadFile(String userName, MultipartFile file) {
        String userFolder = userName + "/";
        String fileName = userFolder + file.getOriginalFilename();

        try {
            // Create a temporary file to upload
            Path tempFile = Files.createTempFile(null, null);
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            PutObjectResponse putResponse = s3Client.putObject(putRequest, tempFile);
            // Delete temporary file
            Files.delete(tempFile);

            return "File uploaded successfully: " + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return "File upload failed: " + e.getMessage();
        }
    }
}
