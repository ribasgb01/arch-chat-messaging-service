package com.microservice.archchatmessagingservice.infrastructure.gateways;

import com.microservice.archchatmessagingservice.application.gateways.FileStorageGateway;
import com.microservice.archchatmessagingservice.domain.Attachment;
import io.minio.*;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class MinioFileStorageAdapter implements FileStorageGateway {

    private final MinioClient minioClient;
    private final String bucketName;

    @Override
    public Attachment uploadFile(InputStream inputStream, long fileSize, String fileName, String contentType, UUID chatId, Double duration) {
        try {
            String fileKey = chatId.toString() + "/" + UUID.randomUUID() + "-" + fileName;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileKey)
                            .stream(inputStream, fileSize, -1L)
                            .contentType(contentType)
                            .build()
            );

            return new Attachment(
                    UUID.randomUUID().toString(),
                    fileName,
                    contentType,
                    fileSize,
                    fileKey,
                    null,
                    duration
            );

        } catch (Exception e) {
            throw new RuntimeException("Erro ao realizar upload do arquivo: " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String fileKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileKey)
                            .build()
            );
        } catch (Exception e){
            throw new RuntimeException("Erro ao deletar arquivo: " + e.getMessage());
        }
    }

    @Override
    public String getPresignedUrl(String fileKey) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Http.Method.GET)
                            .bucket(bucketName)
                            .object(fileKey)
                            .expiry(15, TimeUnit.MINUTES)
                            .build()
            );
        } catch (Exception e){
            throw new RuntimeException("Erro ao gerar URL pré-assinada: " + e.getMessage());
        }
    }
}
