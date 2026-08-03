package com.microservice.archchatmessagingservice.controller;

import com.microservice.archchatmessagingservice.application.usecases.MessageUseCase;
import com.microservice.archchatmessagingservice.application.usecases.dto.request.SendMessageInput;
import com.microservice.archchatmessagingservice.controller.dto.receiver.MessageResponse;
import com.microservice.archchatmessagingservice.controller.dto.request.SendMessageRequest;
import com.microservice.archchatmessagingservice.domain.Message;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chats/{chatId}/messages")
public class MessageController {

    private final MessageUseCase messageUseCase;

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(@PathVariable UUID chatId, @Valid @RequestBody SendMessageRequest request){
        SendMessageInput input = new SendMessageInput(
                chatId,
                request.senderId(),
                request.content(),
                request.type(),
                request.attachment()
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
