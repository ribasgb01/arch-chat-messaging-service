package com.microservice.archchatmessagingservice.application.usecases.dto.request;

import java.util.UUID;

public record SendFriendRequestInput(
        UUID requesterId,
        UUID receiverId
){}
