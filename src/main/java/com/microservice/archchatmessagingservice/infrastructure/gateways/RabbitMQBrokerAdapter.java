package com.microservice.archchatmessagingservice.infrastructure.gateways;

import com.microservice.archchatmessagingservice.application.gateways.MessagePublisherGateway;
import com.microservice.archchatmessagingservice.domain.Message;
import com.microservice.archchatmessagingservice.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RabbitMQBrokerAdapter implements MessagePublisherGateway {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishMessage(Message message) {

        String routingKey = "chat.message." + message.getChatId().toString();

        rabbitTemplate.convertAndSend(RabbitMQConfig.CHAT_EXCHANGE, routingKey, message);
        System.out.println("Mensagem enviada com sucesso para o RabbitMQ na routing key: " + routingKey);
    }

}
