package com.microservice.archchatmessagingservice.controller;

import com.microservice.archchatmessagingservice.application.usecases.ChatUseCase;
import com.microservice.archchatmessagingservice.controller.dto.receiver.ChatResponse;
import com.microservice.archchatmessagingservice.controller.dto.request.CreateChatRequest;
import com.microservice.archchatmessagingservice.domain.Chat;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatUseCase chatUseCase;

    @PostMapping("/create")
    public ResponseEntity<ChatResponse> createChatRoom(@RequestBody @Valid CreateChatRequest request){

        Chat createdChat =  chatUseCase.createChat(request.user1(), request.user2());

        ChatResponse response = new ChatResponse(
                createdChat.getId(),
                createdChat.getCreatedAt(),
                createdChat.getParticipantIds(),
                createdChat.getType(),
                createdChat.getLastMessage()
        );

        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<List<ChatResponse>> getChats(@RequestParam UUID userId){

        List<Chat> chatList = chatUseCase.getChatsByUserId(userId);

        List<ChatResponse> response = chatList.stream()
                .map(domain -> new ChatResponse(
                        domain.getId(),
                        domain.getCreatedAt(),
                        domain.getParticipantIds(),
                        domain.getType(),
                        domain.getLastMessage()
                )).toList();

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> deleteChat(@PathVariable UUID chatId, @RequestParam UUID userId){

        chatUseCase.deleteChat(userId, chatId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
