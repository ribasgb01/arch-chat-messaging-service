package com.microservice.archchatmessagingservice.application.gateways;

import com.microservice.archchatmessagingservice.domain.Message;

public interface MessagePublisherGateway {

    void publishMessage(Message message);
}
