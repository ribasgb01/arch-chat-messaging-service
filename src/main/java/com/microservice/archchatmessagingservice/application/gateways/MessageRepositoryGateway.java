package com.microservice.archchatmessagingservice.application.gateways;

import com.microservice.archchatmessagingservice.domain.Message;

import java.util.List;
import java.util.Optional;

public interface MessageRepositoryGateway {

    Message save(Message message);
    List<Message> findMessagesByChatId(String chatId);
    Optional<Message> findById(String messageId);
}
