package com.microservice.archchatmessagingservice.infrastructure.config;

import com.microservice.archchatmessagingservice.application.gateways.FileStorageGateway;
import com.microservice.archchatmessagingservice.application.usecases.FriendshipUseCase;
import com.microservice.archchatmessagingservice.application.usecases.MessageUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.microservice.archchatmessagingservice.application.gateways.ChatRepositoryGateway;
import com.microservice.archchatmessagingservice.application.gateways.FriendshipRepositoryGateway;
import com.microservice.archchatmessagingservice.application.gateways.MessageRepositoryGateway;
import com.microservice.archchatmessagingservice.application.usecases.ChatUseCase;

@Configuration
public class UseCaseConfig {

    @Bean
    public ChatUseCase chatUseCase(
        ChatRepositoryGateway chatRepositoryGateway,
        FriendshipRepositoryGateway friendshipRepositoryGateway,
        MessageRepositoryGateway messageRepositoryGateway) {
            return new ChatUseCase(chatRepositoryGateway, friendshipRepositoryGateway, messageRepositoryGateway);
        }

    @Bean
    public FriendshipUseCase sendFriendRequestUseCase(FriendshipRepositoryGateway friendshipRepositoryGateway){
        return new FriendshipUseCase(friendshipRepositoryGateway);
    }

    @Bean
    public MessageUseCase messageUseCase(
            ChatRepositoryGateway chatRepositoryGateway,
            MessageRepositoryGateway messageRepositoryGateway,
            FileStorageGateway fileStorageGateway
    ) {
        return new MessageUseCase(chatRepositoryGateway, messageRepositoryGateway, fileStorageGateway);
    }

}
