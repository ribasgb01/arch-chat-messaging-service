package com.microservice.archchatmessagingservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.microservice.archchatmessagingservice.application.gateways.ChatRepositoryGateway;
import com.microservice.archchatmessagingservice.application.gateways.FriendshipRepositoryGateway;
import com.microservice.archchatmessagingservice.application.gateways.MessageRepositoryGateway;
import com.microservice.archchatmessagingservice.application.usecases.ChatUseCase;

@Configuration
public class ChatConfig {

    @Bean
    public ChatUseCase chatUseCase(
        ChatRepositoryGateway chatRepositoryGateway, 
        FriendshipRepositoryGateway friendshipRepositoryGateway, 
        MessageRepositoryGateway messageRepositoryGateway) {
            return new ChatUseCase(chatRepositoryGateway, friendshipRepositoryGateway, messageRepositoryGateway);
        }
}
