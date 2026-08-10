package com.microservice.archchatmessagingservice.controller;

import com.microservice.archchatmessagingservice.application.usecases.ChatUseCase;
import com.microservice.archchatmessagingservice.controller.dto.receiver.ChatResponse;
import com.microservice.archchatmessagingservice.controller.dto.request.CreateChatRequest;
import com.microservice.archchatmessagingservice.domain.Chat;
import com.microservice.archchatmessagingservice.infrastructure.config.UserAuthenticated;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<ChatResponse> createChatRoom(@RequestBody @Valid CreateChatRequest request, @AuthenticationPrincipal UserAuthenticated loggedInUser){

        Chat createdChat =  chatUseCase.createChat(loggedInUser.id(), request.user2());

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
    public ResponseEntity<List<ChatResponse>> getChats(@AuthenticationPrincipal UserAuthenticated loggedInUser){

        List<Chat> chatList = chatUseCase.getChatsByUserId(loggedInUser.id());

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
    public ResponseEntity<Void> deleteChat(@PathVariable UUID chatId, @AuthenticationPrincipal UserAuthenticated loggedInUser){

        chatUseCase.deleteChat(loggedInUser.id(), chatId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
