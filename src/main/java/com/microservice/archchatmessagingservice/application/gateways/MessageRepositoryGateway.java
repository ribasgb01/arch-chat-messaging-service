package com.microservice.archchatmessagingservice.application.gateways;

import com.microservice.archchatmessagingservice.domain.Message;
import com.microservice.archchatmessagingservice.infrastructure.persistence.entities.MessageDocument;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepositoryGateway {

    Message save(Message message);
    List<Message> findMessagesByChatId(UUID chatId);
    Optional<Message> findById(UUID messageId);
}
