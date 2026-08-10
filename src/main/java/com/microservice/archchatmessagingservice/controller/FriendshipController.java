package com.microservice.archchatmessagingservice.controller;

import com.microservice.archchatmessagingservice.application.usecases.FriendshipUseCase;
import com.microservice.archchatmessagingservice.application.usecases.dto.*;
import com.microservice.archchatmessagingservice.controller.dto.receiver.FriendshipResponse;
import com.microservice.archchatmessagingservice.controller.dto.request.*;
import com.microservice.archchatmessagingservice.domain.Friendship;
import com.microservice.archchatmessagingservice.infrastructure.config.UserAuthenticated;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/friendships")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipUseCase friendshipUsecase;

    @PostMapping("/request")
    public ResponseEntity<FriendshipResponse> sendFriendRequest(
            @Valid @RequestBody FriendshipRequest request,
            @AuthenticationPrincipal UserAuthenticated loggedInUser){

        SendFriendRequestInput input = new SendFriendRequestInput(
            loggedInUser.id(),
            request.receiverId()
        );

        Friendship domain = friendshipUsecase.sendFriendRequest(input);

        FriendshipResponse response = new FriendshipResponse(
                domain.getId(),
                domain.getRequesterId(),
                domain.getReceiverId(),
                domain.getStatus(),
                domain.getCreatedAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{friendshipId}/accept")
    public ResponseEntity<FriendshipResponse> acceptRequest(@AuthenticationPrincipal UserAuthenticated loggedInUser, @PathVariable UUID friendshipId){

        AcceptFriendRequestInput input = new AcceptFriendRequestInput(
                friendshipId,
                loggedInUser.id()

        );

        Friendship domain = friendshipUsecase.acceptRequest(input);

        FriendshipResponse response = new FriendshipResponse(
                domain.getId(),
                domain.getRequesterId(),
                domain.getReceiverId(),
                domain.getStatus(),
                domain.getCreatedAt()
        );

        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{friendshipId}/decline")
    public ResponseEntity<FriendshipResponse> declineRequest(@AuthenticationPrincipal UserAuthenticated loggedInUser, @PathVariable UUID friendshipId){

        DeclineFriendRequestInput input = new DeclineFriendRequestInput(
                friendshipId,
                loggedInUser.id()
        );

        Friendship domain = friendshipUsecase.declineRequest(input);

        FriendshipResponse response = new FriendshipResponse(
                domain.getId(),
                domain.getRequesterId(),
                domain.getReceiverId(),
                domain.getStatus(),
                domain.getCreatedAt()
        );

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/block")
    public ResponseEntity<FriendshipResponse> blockUser(@Valid @RequestBody BlockUserRequest request, @AuthenticationPrincipal UserAuthenticated loggedInUser) {

        BlockUserInput input = new BlockUserInput(
                loggedInUser.id(),
                request.blockedId()
        );

        Friendship domain = friendshipUsecase.blockUser(input);

        FriendshipResponse response = new FriendshipResponse(
                domain.getId(),
                domain.getRequesterId(),
                domain.getReceiverId(),
                domain.getStatus(),
                domain.getCreatedAt()
        );

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/unblock")
    public ResponseEntity<FriendshipResponse> unblockUser(@RequestBody @Valid UnblockUserRequest request,@AuthenticationPrincipal UserAuthenticated loggedInUser){

        UnblockUserInput input = new UnblockUserInput(
                loggedInUser.id(),
                request.blockedId()
        );

        Friendship domain = friendshipUsecase.unblockUser(input);

        FriendshipResponse response = new FriendshipResponse(
                domain.getId(),
                domain.getRequesterId(),
                domain.getReceiverId(),
                domain.getStatus(),
                domain.getCreatedAt()
        );

        return ResponseEntity.ok().body(response);
    }


    @GetMapping
    public ResponseEntity<List<UUID>> getAcceptedFriendships(@AuthenticationPrincipal UserAuthenticated loggedInUser) {
        List<UUID> friends = friendshipUsecase.getAcceptedFriendships(loggedInUser.id());
        return ResponseEntity.ok(friends);
    }

}
