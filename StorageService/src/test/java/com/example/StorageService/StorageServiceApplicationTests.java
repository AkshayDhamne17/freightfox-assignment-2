package com.example.StorageService;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.web.multipart.MultipartFile;

import com.example.StorageService.service.S3Service;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

@SpringBootTest
class StorageServiceApplicationTests {

	@Mock
    private S3Client s3Client;

    @InjectMocks
    private S3Service s3Service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSearchFiles() {
        // Mock the S3 objects and the S3 response
        S3Object s3Object1 = S3Object.builder().key("sandy/logistics1.txt").build();
        S3Object s3Object2 = S3Object.builder().key("sandy/logistics2.pdf").build();
        ListObjectsV2Response response = ListObjectsV2Response.builder()
                .contents(Arrays.asList(s3Object1, s3Object2))
                .build();

        // Mock S3Client behavior
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(response);

        // Call the method under test
        List<String> result = s3Service.searchFiles("sandy", "logistics");

        // Verify the result
        assertEquals(2, result.size());
        assertTrue(result.contains("sandy/logistics1.txt"));
        assertTrue(result.contains("sandy/logistics2.pdf"));

        // Verify that the mock S3 client was called
        verify(s3Client, times(1)).listObjectsV2(any(ListObjectsV2Request.class));
    }

    @Test
    public void testUploadFile() throws IOException {
        // Mock MultipartFile
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("testfile.txt");
        when(mockFile.getInputStream()).thenReturn(getClass().getResourceAsStream("/testfile.txt"));

        // Mock S3Client response
        PutObjectResponse mockResponse = PutObjectResponse.builder().build();
        
        // Create a RequestBody from the input stream
        RequestBody requestBody = RequestBody.fromInputStream(mockFile.getInputStream(), mockFile.getSize());

        // Mock the putObject method to return the mocked response
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(mockResponse);

        // Call the method under test
        String result = s3Service.uploadFile("sandy", mockFile);

        // Verify the result
        assertTrue(result.contains("File uploaded successfully"));

        // Verify S3 client interaction
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
}
