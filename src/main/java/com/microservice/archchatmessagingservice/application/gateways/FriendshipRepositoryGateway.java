package com.microservice.archchatmessagingservice.application.gateways;

import com.microservice.archchatmessagingservice.domain.Friendship;

import java.util.Optional;
import java.util.UUID;

public interface FriendshipRepositoryGateway {
    Friendship save (Friendship friendship);

    Optional<Friendship> findById(UUID id);

    Optional<Friendship> findRelationBetween (UUID user1, UUID user2);
}
