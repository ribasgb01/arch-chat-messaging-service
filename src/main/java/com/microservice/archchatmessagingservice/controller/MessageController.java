package com.microservice.archchatmessagingservice.controller;

import com.microservice.archchatmessagingservice.application.usecases.MessageUseCase;
import com.microservice.archchatmessagingservice.application.usecases.dto.request.DeleteMessageInput;
import com.microservice.archchatmessagingservice.application.usecases.dto.request.SendAudioInput;
import com.microservice.archchatmessagingservice.application.usecases.dto.request.SendMessageInput;
import com.microservice.archchatmessagingservice.controller.dto.receiver.MessageResponse;
import com.microservice.archchatmessagingservice.controller.dto.request.SendAudioRequest;
import com.microservice.archchatmessagingservice.controller.dto.request.SendMessageRequest;
import com.microservice.archchatmessagingservice.domain.Message;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chats/{chatId}/messages")
public class MessageController {

    private final MessageUseCase messageUseCase;

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable UUID chatId,
            @Valid @RequestPart("message") SendMessageRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {

        InputStream fileStream = null;
        Long fileSize = null;
        String fileName = null;
        String contentType = null;

        if (file != null || !file.isEmpty()){
            fileStream = file.getInputStream();
            fileSize = file.getSize();
            fileName = file.getOriginalFilename();
            contentType = file.getContentType();
        }

        SendMessageInput input = new SendMessageInput(
                chatId,
                request.senderId(),
                request.content(),
                request.type(),
                fileStream,
                fileSize,
                fileName,
                contentType
        );

        Message savedMessage = messageUseCase.saveMessage(input);

        MessageResponse response = new MessageResponse(
                savedMessage.getId(),
                savedMessage.getChatId(),
                savedMessage.getSenderId(),
                savedMessage.getContent(),
                savedMessage.getTimestamp(),
                savedMessage.isEdited(),
                savedMessage.getAttachment(),
                savedMessage.getStatus(),
                savedMessage.getType()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/audio")
    public ResponseEntity<MessageResponse> sendAudio(
            @PathVariable UUID chatId,
            @RequestPart("audio") @Valid SendAudioRequest request,
            @RequestPart("file") MultipartFile file
            ) throws IOException {

        InputStream inputStream = file.getInputStream();
        long fileSize = file.getSize();
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();

        SendAudioInput input = new SendAudioInput(
                chatId,
                request.senderId(),
                inputStream,
                fileSize,
                fileName,
                contentType,
                request.duration()
        );

        Message savedMessage = messageUseCase.sendAudio(input);

        MessageResponse response = new MessageResponse(
                savedMessage.getId(),
                savedMessage.getChatId(),
                savedMessage.getSenderId(),
                savedMessage.getContent(),
                savedMessage.getTimestamp(),
                savedMessage.isEdited(),
                savedMessage.getAttachment(),
                savedMessage.getStatus(),
                savedMessage.getType()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable UUID chatId,
            @PathVariable UUID messageId,
            @RequestParam UUID userId
    ) {
        DeleteMessageInput input = new DeleteMessageInput(messageId, userId);

        messageUseCase.deleteMessage(input);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<MessageResponse>> getChatHistory(@PathVariable UUID chatId, @RequestParam UUID userId){

        List<MessageResponse> response = messageUseCase.getChatHistory(chatId, userId).stream()
                .map(messageDomain -> new MessageResponse(
                        messageDomain.getId(),
                        messageDomain.getChatId(),
                        messageDomain.getSenderId(),
                        messageDomain.getContent(),
                        messageDomain.getTimestamp(),
                        messageDomain.isEdited(),
                        messageDomain.getAttachment(),
                        messageDomain.getStatus(),
                        messageDomain.getType()
                )).toList();

        return ResponseEntity.ok(response);
    }
}
