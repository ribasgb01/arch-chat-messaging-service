package com.microservice.archchatmessagingservice.application.usecases;

import com.microservice.archchatmessagingservice.application.exceptions.ChatNotFoundException;
import com.microservice.archchatmessagingservice.application.exceptions.InvalidMessageStateException;
import com.microservice.archchatmessagingservice.application.exceptions.MessageNotFoundException;
import com.microservice.archchatmessagingservice.application.exceptions.UnauthorizedActionException;
import com.microservice.archchatmessagingservice.application.gateways.ChatRepositoryGateway;
import com.microservice.archchatmessagingservice.application.gateways.FileStorageGateway;
import com.microservice.archchatmessagingservice.application.gateways.MessagePublisherGateway;
import com.microservice.archchatmessagingservice.application.gateways.MessageRepositoryGateway;
import com.microservice.archchatmessagingservice.application.usecases.dto.request.DeleteMessageInput;
import com.microservice.archchatmessagingservice.application.usecases.dto.request.SendAudioInput;
import com.microservice.archchatmessagingservice.application.usecases.dto.request.SendMessageInput;
import com.microservice.archchatmessagingservice.domain.Attachment;
import com.microservice.archchatmessagingservice.domain.Chat;
import com.microservice.archchatmessagingservice.domain.LastMessage;
import com.microservice.archchatmessagingservice.domain.Message;
import com.microservice.archchatmessagingservice.domain.enums.MessageStatus;
import com.microservice.archchatmessagingservice.domain.enums.MessageType;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class MessageUseCase {

    private final ChatRepositoryGateway chatRepository;
    private final MessageRepositoryGateway messageRepository;
    private final FileStorageGateway fileStorage;
    private final MessagePublisherGateway publisherGateway;

    public Message saveMessage(SendMessageInput input){

        Chat chat = chatRepository.findById(input.chatId())
                .orElseThrow(() -> new ChatNotFoundException("Sala de chat não foi encontrada"));

        if(!chat.getParticipantIds().contains(input.senderId())){
            throw new UnauthorizedActionException("Usuário não tem permissão para enviar mensagens nesta conversa");
        }

        Attachment updatedAttachment = null;

        if(input.fileStream() != null){
            Attachment attachment = fileStorage.uploadFile(
                    input.fileStream(),
                    input.fileSize(),
                    input.fileName(),
                    input.contentType(),
                    input.chatId(),
                    null
            );

            String tempUrl = fileStorage.getPresignedUrl(attachment.getKey());

             updatedAttachment = new Attachment(
                    attachment.getId(),
                    attachment.getFileName(),
                    attachment.getContentType(),
                    attachment.getSize(),
                    attachment.getKey(),
                    tempUrl,
                    attachment.getDuration()
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
                .attachment(updatedAttachment)
                .isEdited(false)
                .build();

        Message savedMessage = messageRepository.save(message);
        publisherGateway.publishMessage(savedMessage);

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

    public Message sendAudio(SendAudioInput input){

        Chat chat = chatRepository.findById(input.chatId())
                .orElseThrow(() -> new ChatNotFoundException("Sala de chat não foi encontrada"));

        if(!chat.getParticipantIds().contains(input.senderId())){
            throw new UnauthorizedActionException("Usuário não tem permissão para enviar mensagens nesta conversa");
        }

        Attachment attachment = fileStorage.uploadFile(
                input.fileStream(),
                input.fileSize(),
                input.fileName(),
                input.contentType(),
                input.chatId(),
                input.duration()
        );

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

        Message message = Message.builder()
                .id(UUID.randomUUID())
                .chatId(input.chatId())
                .senderId(input.senderId())
                .content("Mensagem de voz")
                .timestamp(LocalDateTime.now())
                .status(MessageStatus.SENT)
                .type(MessageType.AUDIO)
                .attachment(updatedAttachment)
                .isEdited(false)
                .build();

        Message savedMessage = messageRepository.save(message);
        publisherGateway.publishMessage(savedMessage);

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

    public void deleteMessage(DeleteMessageInput input){

        Message message = messageRepository.findById(input.messageId())
                .orElseThrow(() -> new MessageNotFoundException("Mensagem não foi encontrada"));

        if(message.getStatus() == MessageStatus.DELETED){
            throw new InvalidMessageStateException("Esta mensagem já foi apagada");
        }

        if(!message.getSenderId().equals(input.userId())){
            throw new UnauthorizedActionException("Usuário não tem permissão para apagar esta mensagme");
        }

        if(message.getAttachment() != null){
            fileStorage.deleteFile(message.getAttachment().getKey());
        }

        message.setStatus(MessageStatus.DELETED);
        message.setContent("");
        message.setAttachment(null);

        messageRepository.save(message);

        chatRepository.findById(message.getChatId()).ifPresent(chat -> {
            if (chat.getLastMessage() != null && chat.getLastMessage().getMessageId().equals(message.getId())) {

                LastMessage updatedLastMessage = new LastMessage(
                        message.getId(),
                        message.getSenderId(),
                        "",
                        message.getTimestamp(),
                        MessageStatus.DELETED
                );

                chat.setLastMessage(updatedLastMessage);
                chatRepository.save(chat);
            }
        });
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
