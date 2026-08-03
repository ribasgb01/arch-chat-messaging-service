package com.microservice.archchatmessagingservice.application.usecases;

import com.microservice.archchatmessagingservice.application.exceptions.ChatNotFoundException;
import com.microservice.archchatmessagingservice.application.exceptions.UnauthorizedActionException;
import com.microservice.archchatmessagingservice.application.gateways.ChatRepositoryGateway;
import com.microservice.archchatmessagingservice.application.gateways.MessageRepositoryGateway;
import com.microservice.archchatmessagingservice.application.usecases.dto.request.SendMessageInput;
import com.microservice.archchatmessagingservice.domain.Chat;
import com.microservice.archchatmessagingservice.domain.LastMessage;
import com.microservice.archchatmessagingservice.domain.Message;
import com.microservice.archchatmessagingservice.domain.enums.MessageStatus;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class MessageUseCase {

    private final ChatRepositoryGateway chatRepository;
    private final MessageRepositoryGateway messageRepository;

    public Message saveMessage(SendMessageInput input){

        Chat chat = chatRepository.findById(input.chatId())
                .orElseThrow(() -> new ChatNotFoundException("Sala de chat não foi encontrada"));

        if(!chat.getParticipantIds().contains(input.senderId())){
            throw new UnauthorizedActionException("Usuário não tem permissão para enviar mensagens nesta conversa");
        }

        Message message = Message.builder()
                .id(UUID.randomUUID())
                .chatId(input.chatId())
                .senderId(input.senderId())
                .content(input.content())
                .timestamp(LocalDateTime.now())
                .status(MessageStatus.SENT)
                .type(input.type())
                .attachment(input.attachment())
                .isEdited(false)
                .build();

        Message savedMessage = messageRepository.save(message);

        LastMessage lastMessage = LastMessage.builder()
                .messageId(savedMessage.getId())
                .senderId(savedMessage.getSenderId())
                .content(savedMessage.getContent())
                .timestamp(savedMessage.getTimestamp())
                .status(savedMessage.getStatus())
                .build();

        chat.setLastMessage(lastMessage);
        chatRepository.save(chat);

        return savedMessage;
    }

    public List<Message> getChatHistory(UUID chatId, UUID userId){

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Sala de chat não encontrada"));

        if(!chat.getParticipantIds().contains(userId)){
            throw new UnauthorizedActionException("Usuário não tem permissão para visualizar o histórico de conversa");
        }

        return messageRepository.findMessagesByChatId(chatId);
    }
}
