package com.microservice.archchatmessagingservice.controller;

import com.microservice.archchatmessagingservice.application.usecases.FriendshipUseCase;
import com.microservice.archchatmessagingservice.application.usecases.dto.request.*;
import com.microservice.archchatmessagingservice.controller.dto.receiver.FriendshipResponse;
import com.microservice.archchatmessagingservice.controller.dto.request.*;
import com.microservice.archchatmessagingservice.domain.Friendship;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/friendships")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipUseCase friendshipUsecase;

    @PostMapping("/request")
    public ResponseEntity<FriendshipResponse> sendFriendRequest(@Valid @RequestBody FriendshipRequest request){

        SendFriendRequestInput input = new SendFriendRequestInput(
            request.requesterId(),
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
    public ResponseEntity<FriendshipResponse> acceptRequest(@RequestBody @Valid AcceptFriendRequestDto request, @PathVariable UUID friendshipId){

        AcceptFriendRequestInput input = new AcceptFriendRequestInput(
                friendshipId,
                request.receiverId()
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
    public ResponseEntity<FriendshipResponse> declineRequest(@RequestBody @Valid DeclineFriendRequestDto request, @PathVariable UUID friendshipId){

        DeclineFriendRequestInput input = new DeclineFriendRequestInput(
                friendshipId,
                request.receiverId()
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
    public ResponseEntity<FriendshipResponse> blockUser(@Valid @RequestBody BlockUserRequest request) {

        BlockUserInput input = new BlockUserInput(
                request.blockerId(),
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
    public ResponseEntity<FriendshipResponse> unblockUser(@RequestBody @Valid UnblockUserRequest request){

        UnblockUserInput input = new UnblockUserInput(
                request.unblockerId(),
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
    public ResponseEntity<List<UUID>> getAcceptedFriendships(@RequestParam UUID userId) {
        List<UUID> friends = friendshipUsecase.getAcceptedFriendships(userId);
        return ResponseEntity.ok(friends);
    }

}
