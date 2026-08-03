package com.microservice.archchatmessagingservice.infrastructure.gateways;

import com.microservice.archchatmessagingservice.application.gateways.FileStorageGateway;
import com.microservice.archchatmessagingservice.domain.Attachment;
import org.springframework.web.multipart.MultipartFile;

public class MinioFileStorageAdapter implements FileStorageGateway {
    @Override
    public Attachment uploadFile(MultipartFile file, String fileName, String contentType, Double duration) {
        return null;
    }

    @Override
    public void deleteFile(String fileKey) {

    }

    @Override
    public String getPreSignedUrl(String fileKey) {
        return "";
    }
}
