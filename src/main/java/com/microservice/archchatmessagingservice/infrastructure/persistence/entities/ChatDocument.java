package com.microservice.archchatmessagingservice.infrastructure.persistence.entities;

import com.microservice.archchatmessagingservice.domain.LastMessage;
import com.microservice.archchatmessagingservice.domain.enums.ChatType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document(collection = "chats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatDocument {

    @Id
    private UUID Id;
    private LocalDateTime createdAt;
    private List<UUID> participantIds;
    private ChatType type;
    private LastMessage lastMessage;
}
