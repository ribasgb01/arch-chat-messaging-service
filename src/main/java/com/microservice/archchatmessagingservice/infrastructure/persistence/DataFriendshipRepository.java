package com.microservice.archchatmessagingservice.infrastructure.persistence;

import com.microservice.archchatmessagingservice.infrastructure.persistence.entities.FriendshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DataFriendshipRepository extends JpaRepository<FriendshipEntity, UUID> {

    @Query("SELECT f FROM FriendshipEntity f WHERE " +
    "(f.requesterId = :user1 AND f.receiverId = :user2) OR" +
    "(f.requesterId = :user2 AND f.receiverId = :user1)")
    Optional<FriendshipEntity> findRelationBetween(
            @Param("user1") UUID user1,
            @Param("user2") UUID user2
    );
}
