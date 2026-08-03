package com.microservice.archchatmessagingservice.application.gateways;

import com.microservice.archchatmessagingservice.domain.Attachment;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageGateway {

    Attachment uploadFile(MultipartFile file, String fileName, String contentType, Double duration);
    void deleteFile(String fileKey);
    String getPreSignedUrl(String fileKey);
}
