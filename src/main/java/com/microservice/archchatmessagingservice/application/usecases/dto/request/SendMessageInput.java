package com.microservice.archchatmessagingservice.application.usecases.dto.request;

import com.microservice.archchatmessagingservice.domain.Attachment;
import com.microservice.archchatmessagingservice.domain.enums.MessageType;

import java.util.UUID;

public record SendMessageInput(
        UUID chatId,
        UUID senderId,
        String content,
        MessageType type,
        Attachment attachment
) {}
