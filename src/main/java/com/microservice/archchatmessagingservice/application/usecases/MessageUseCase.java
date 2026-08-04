package com.microservice.archchatmessagingservice.application.usecases;

import com.microservice.archchatmessagingservice.application.exceptions.ChatNotFoundException;
import com.microservice.archchatmessagingservice.application.exceptions.UnauthorizedActionException;
import com.microservice.archchatmessagingservice.application.gateways.ChatRepositoryGateway;
import com.microservice.archchatmessagingservice.application.gateways.FileStorageGateway;
import com.microservice.archchatmessagingservice.application.gateways.MessageRepositoryGateway;
import com.microservice.archchatmessagingservice.application.usecases.dto.request.SendMessageInput;
import com.microservice.archchatmessagingservice.domain.Attachment;
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
    private final FileStorageGateway fileStorage;

    public Message saveMessage(SendMessageInput input){

        Chat chat = chatRepository.findById(input.chatId())
                .orElseThrow(() -> new ChatNotFoundException("Sala de chat não foi encontrada"));

        if(!chat.getParticipantIds().contains(input.senderId())){
            throw new UnauthorizedActionException("Usuário não tem permissão para enviar mensagens nesta conversa");
        }

        Attachment attachment = null;

        if(input.fileStream() != null){
            attachment = fileStorage.uploadFile(
                    input.fileStream(),
                    input.fileSize(),
                    input.fileName(),
                    input.contentType(),
                    input.chatId(),
                    null
            );
        }

        Message message = Message.builder()
                .id(UUID.randomUUID())
                .chatId(input.chatId())
                .senderId(input.senderId())
                .content(input.content())
                .timestamp(LocalDateTime.now())
                .status(MessageStatus.SENT)
                .type(input.type())
                .attachment(attachment)
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

        return messageRepository.findMessagesByChatId(chatId).stream()
                .map(message -> {
                    if (message.getAttachment() != null){

                        Attachment attachment = message.getAttachment();
                        String tempUrl = fileStorage.getPresignedUrl(attachment.getKey());

                        Attachment updatedAttachment = new Attachment(
                                attachment.getId(),
                                attachment.getFileName(),
                                attachment.getContentType(),
                                attachment.getSize(),
                                attachment.getKey(),
                                tempUrl,
                                attachment.getDuration()
                        );

                        message.setAttachment(updatedAttachment);
                    }
                    return message;
        }).toList();

    }
}
