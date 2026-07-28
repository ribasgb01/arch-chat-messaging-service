package com.microservice.archchatmessagingservice.application.usecases;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.microservice.archchatmessagingservice.application.exceptions.AlreadyAreFriendsException;
import com.microservice.archchatmessagingservice.application.exceptions.CannotAddSelfException;
import com.microservice.archchatmessagingservice.application.exceptions.RequestAlreadyPendingException;
import com.microservice.archchatmessagingservice.application.gateways.FriendshipRepositoryGateway;
import com.microservice.archchatmessagingservice.application.usecases.dto.SendFriendRequestInput;
import com.microservice.archchatmessagingservice.domain.Friendship;
import com.microservice.archchatmessagingservice.domain.enums.FriendshipStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SendFriendRequestUseCase {

    private final FriendshipRepositoryGateway friendshipRepositoryGateway;

    public Friendship execute(SendFriendRequestInput input){

        UUID requester = input.requesterId();
        UUID receiver = input.receiverId();

        if (requester.equals(receiver)){
            throw new CannotAddSelfException("Não é possível enviar uma solicitação de amizade para si mesmo");
        }
    
        Optional<Friendship> existingRelation = friendshipRepositoryGateway.findRelationBetween(requester, receiver);

        if(existingRelation.isPresent()){
            Friendship relation = existingRelation.get();
            return handleExistingRelation(relation, requester, receiver);
        }

        Friendship newRequest = Friendship.builder()
                .requesterId(requester)
                .receiverId(receiver)
                .status(FriendshipStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .blockedBy(null)
                .build();

        return friendshipRepositoryGateway.save(newRequest);
    }

    private Friendship handleExistingRelation(Friendship relation, UUID requester, UUID receiver){
        if(relation.getStatus() == FriendshipStatus.ACCEPTED){
            throw new AlreadyAreFriendsException("Vocês já são amigos");
        }

        if(relation.getStatus() == FriendshipStatus.PENDING){
            if(relation.getRequesterId() == requester){
                throw new RequestAlreadyPendingException("Você já enviou uma solicitação de amizade para esse usuário");
            } else {
                throw new RequestAlreadyPendingException("Este usuário já te enviou uma solicitação de amizade");
            }
        }

        if(relation.getStatus() == FriendshipStatus.DECLINED){

            relation.setStatus(FriendshipStatus.PENDING);
            relation.setRequesterId(requester);
            relation.setReceiverId(receiver);
            relation.setBlockedBy(null);
            relation.setCreatedAt(LocalDateTime.now());            
        }

        return friendshipRepositoryGateway.save(relation);
    }

}
