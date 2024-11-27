package com.example.imageservice.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
public class ImageService {

    @Autowired
    private GridFSBucket gridFSBucket;

    // Upload a file
    public String uploadFile(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        ObjectId fileId = gridFSBucket.uploadFromStream(file.getOriginalFilename(), inputStream);
        return fileId.toString();
    }

    // Download a file
    public Optional<byte[]> downloadFile(String id) {
        GridFSFile gridFSFile = gridFSBucket.find(new org.bson.Document("_id", new ObjectId(id))).first();
        if (gridFSFile != null) {
            try (InputStream inputStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId())) {
                return Optional.of(inputStream.readAllBytes());
            } catch (IOException e) {
                throw new RuntimeException("Error reading file", e);
            }
        }
        return Optional.empty();
    }

    // Delete a file
    public void deleteFile(String id) {
        gridFSBucket.delete(new ObjectId(id));
    }
}
