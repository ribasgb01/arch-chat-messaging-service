package com.microservice.archchatmessagingservice.controller.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SendAudioRequest(
        @NotNull
        UUID senderId,

        @NotNull
        Double duration
) {
}
