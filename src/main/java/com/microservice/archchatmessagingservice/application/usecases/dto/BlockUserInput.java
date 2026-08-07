package com.microservice.archchatmessagingservice.application.usecases.dto.request;

import java.util.UUID;

public record BlockUserInput (
        UUID blockerId,
        UUID blockedId
) {}
