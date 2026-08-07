package com.microservice.archchatmessagingservice.application.usecases.dto.request;

import java.util.UUID;

public record DeleteMessageInput(
        UUID messageId,
        UUID userId
) {
}
