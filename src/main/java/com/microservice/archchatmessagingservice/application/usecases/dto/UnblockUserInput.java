package com.microservice.archchatmessagingservice.application.usecases.dto.request;

import java.util.UUID;

public record UnblockUserInput(
        UUID unblockerId,
        UUID blockedId
) {}
