package com.microservice.archchatmessagingservice.application.usecases;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.microservice.archchatmessagingservice.application.exceptions.*;
import com.microservice.archchatmessagingservice.application.gateways.FriendshipRepositoryGateway;
import com.microservice.archchatmessagingservice.application.usecases.dto.request.AcceptFriendRequestInput;
import com.microservice.archchatmessagingservice.application.usecases.dto.request.BlockUserInput;
import com.microservice.archchatmessagingservice.application.usecases.dto.request.DeclineFriendRequestInput;
import com.microservice.archchatmessagingservice.application.usecases.dto.request.SendFriendRequestInput;
import com.microservice.archchatmessagingservice.domain.Friendship;
import com.microservice.archchatmessagingservice.domain.enums.FriendshipStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FriendshipUseCase {

    private final FriendshipRepositoryGateway friendshipRepositoryGateway;

    public Friendship sendFriendRequest(SendFriendRequestInput input) {
        UUID requester = input.requesterId();
        UUID receiver = input.receiverId();

        if (requester.equals(receiver)) {
            throw new InvalidSelfActionException("Não é possível enviar uma solicitação de amizade para si mesmo");
        }

        Optional<Friendship> existingRelation = friendshipRepositoryGateway.findRelationBetween(requester, receiver);

        if (existingRelation.isPresent()) {
            Friendship relation = existingRelation.get();

            boolean blockedByUser = (relation.getRequesterId().equals(requester) && relation.isBlockedByRequester()) ||
                    (relation.getReceiverId().equals(requester) && relation.isBlockedByReceiver());

            boolean blockedByFriend = (relation.getRequesterId().equals(receiver) && relation.isBlockedByRequester()) ||
                    (relation.getReceiverId().equals(receiver) && relation.isBlockedByReceiver());

            if (relation.getStatus() == FriendshipStatus.ACCEPTED) {
                throw new AlreadyAreFriendsException("Vocês já são amigos");
            }

            if (relation.getStatus() == FriendshipStatus.PENDING) {
                if (relation.getRequesterId().equals(requester)) {
                    throw new RequestAlreadyPendingException("Você já enviou uma solicitação de amizade para esse usuário");
                } else {
                    throw new RequestAlreadyPendingException("Este usuário já te enviou uma solicitação de amizade");
                }
            }

            if (relation.getStatus() == FriendshipStatus.BLOCKED) {
                if (blockedByUser) {
                    throw new UserBlockedException("Você bloqueou este usuário. Desbloqueie-o primeiro.");
                }
                if (blockedByFriend) {
                    throw new UserBlockedException("Não foi possível enviar a solicitação de amizade.");
                }
            }

            if (relation.getStatus() == FriendshipStatus.DECLINED) {
                relation.setStatus(FriendshipStatus.PENDING);
                relation.setRequesterId(requester);
                relation.setReceiverId(receiver);
                relation.setBlockedByRequester(false);
                relation.setBlockedByReceiver(false);
                relation.setCreatedAt(LocalDateTime.now());
            }

            return friendshipRepositoryGateway.save(relation);
        }

        Friendship newRequest = Friendship.builder()
                .requesterId(requester)
                .receiverId(receiver)
                .status(FriendshipStatus.PENDING)
                .isBlockedByRequester(false)
                .isBlockedByReceiver(false)
                .createdAt(LocalDateTime.now())
                .build();

        return friendshipRepositoryGateway.save(newRequest);
    }

    public Friendship declineRequest(DeclineFriendRequestInput input){
        Friendship friendship = friendshipRepositoryGateway.findById(input.friendshipId())
                .orElseThrow(() -> new FriendshipNotFoundException("Solicitação de amizade não encontrada"));

        if(friendship.getStatus() != FriendshipStatus.PENDING){
            throw new InvalidFriendshipStateException("Esta solicitação não está pendente");
        }

        if(!input.receiverId().equals(friendship.getReceiverId())){
            throw new NotAuthorizedToAcceptException("Você não tem permissão para recusar essa solicitação");
        }

        friendship.setStatus(FriendshipStatus.DECLINED);

        return friendshipRepositoryGateway.save(friendship);
    }

    public Friendship acceptRequest(AcceptFriendRequestInput input){
        Friendship friendship = friendshipRepositoryGateway.findById(input.friendshipId())
                .orElseThrow(() -> new FriendshipNotFoundException("Solicitação de amizade não encontrada"));

        if(friendship.getStatus() != FriendshipStatus.PENDING){
            throw new InvalidFriendshipStateException("Esta solicitação não está pendente");
        }

        if(!input.receiverId().equals(friendship.getReceiverId())){
            throw new NotAuthorizedToAcceptException("Você não tem permissão para aceitar essa solicitação");
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);

        return friendshipRepositoryGateway.save(friendship);
    }

    public Friendship blockUser(BlockUserInput input){
        if(input.blockedId().equals(input.blockerId())){
            throw new InvalidSelfActionException("Não é possível bloquear a sí mesmo");
        }

        Optional<Friendship> existingRelation = friendshipRepositoryGateway.findRelationBetween(input.blockedId(), input.blockerId());

        if(existingRelation.isPresent()){
            Friendship relation = existingRelation.get();

            if (relation.getRequesterId().equals(input.blockerId())) {
                relation.setBlockedByRequester(true);
            } else {
                relation.setBlockedByReceiver(true);
            }

            relation.setStatus(FriendshipStatus.BLOCKED);
            return friendshipRepositoryGateway.save(relation);
        }

        return friendshipRepositoryGateway.save(Friendship.builder()
                .requesterId(input.blockerId())
                .receiverId(input.blockedId())
                .status(FriendshipStatus.BLOCKED)
                .isBlockedByRequester(true)
                .isBlockedByRequester(false)
                .createdAt(LocalDateTime.now())
                .build());

    }

}
