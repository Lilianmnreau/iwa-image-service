package com.example.imageservice.controller;

import com.example.imageservice.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileId = imageService.uploadFile(file);
            return ResponseEntity.ok(fileId);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable String id) {
        Optional<byte[]> fileData = imageService.downloadFile(id);
        if (fileData.isPresent()) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + id + "\"");
            return new ResponseEntity<>(fileData.get(), headers, HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable String id) {
        imageService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }
}
