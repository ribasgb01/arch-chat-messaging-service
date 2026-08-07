package com.microservice.archchatmessagingservice.controller;

import com.microservice.archchatmessagingservice.application.usecases.MessageUseCase;
import com.microservice.archchatmessagingservice.application.usecases.dto.DeleteMessageInput;
import com.microservice.archchatmessagingservice.application.usecases.dto.EditMessageInput;
import com.microservice.archchatmessagingservice.application.usecases.dto.SendMessageInput;
import com.microservice.archchatmessagingservice.controller.dto.request.EditMessageRequest;
import com.microservice.archchatmessagingservice.controller.dto.request.SendMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

    private final MessageUseCase messageUseCase;

    @MessageMapping("/chat/{chatId}/sendMessage")
    public void sendMessage(
            @DestinationVariable UUID chatId,
            @Payload SendMessageRequest request
            ){

        SendMessageInput input = new SendMessageInput(
                chatId,
                request.senderId(),
                request.content(),
                request.type(),
                null, null, null, null
        );

        messageUseCase.saveMessage(input);
    }

    @MessageMapping("/chat/{chatId}/deleteMessage")
    public void deleteMessage(
            @DestinationVariable UUID chatId,
            @Payload UUID userId,
            @Payload UUID messageId
        ){
        DeleteMessageInput input = new DeleteMessageInput(
                messageId,
                userId
        );

        messageUseCase.deleteMessage(input);
    }

    @MessageMapping("/chat/{chatId}/editMessage")
    public void editMessage(
            @DestinationVariable UUID chatId,
            @Payload EditMessageRequest request
        ){
        EditMessageInput input = new EditMessageInput(
                request.senderId(),
                request.messageId(),
                request.content()
        );

        messageUseCase.editMessage(input);

    }
}
