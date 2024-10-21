package com.example.StorageService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.StorageService.service.S3Service;

@RestController
@RequestMapping("/api/files")
public class FileController {
	private final S3Service s3Service;

    public FileController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchFiles(@RequestParam String userName, @RequestParam String fileName) {
        return ResponseEntity.ok(s3Service.searchFiles(userName, fileName));
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam String userName, @RequestParam MultipartFile file) {
        String fileUrl = s3Service.uploadFile(userName, file);
        return ResponseEntity.ok(fileUrl);
    }
}
