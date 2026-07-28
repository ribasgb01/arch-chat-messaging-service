package com.microservice.archchatmessagingservice.infrastructure.config;

import com.microservice.archchatmessagingservice.application.gateways.FriendshipRepositoryGateway;
import com.microservice.archchatmessagingservice.application.usecases.SendFriendRequestUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FriendshipConfig {

    @Bean
    public SendFriendRequestUseCase sendFriendRequestUseCase(FriendshipRepositoryGateway friendshipRepositoryGateway){
        return new SendFriendRequestUseCase(friendshipRepositoryGateway);
    }
}
