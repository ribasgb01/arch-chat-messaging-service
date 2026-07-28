package com.microservice.archchatmessagingservice.controller;

import com.microservice.archchatmessagingservice.application.usecases.SendFriendRequestUseCase;
import com.microservice.archchatmessagingservice.application.usecases.dto.SendFriendRequestInput;
import com.microservice.archchatmessagingservice.controller.dto.receiver.FriendshipResponse;
import com.microservice.archchatmessagingservice.controller.dto.request.FriendshipRequest;
import com.microservice.archchatmessagingservice.domain.Friendship;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/friendships")
@RequiredArgsConstructor
public class FriendshipController {

    private final SendFriendRequestUseCase sendFriendRequestUseCase;

    @PostMapping("/request")
    public ResponseEntity<FriendshipResponse> sendFriendRequest(@Valid @RequestBody FriendshipRequest request){

        SendFriendRequestInput input = new SendFriendRequestInput(
            request.requesterId(),
            request.receiverId()
        );

        Friendship domain = sendFriendRequestUseCase.execute(input);

        FriendshipResponse savedResponse = new FriendshipResponse(
                domain.getId(),
                domain.getRequesterId(),
                domain.getReceiverId(),
                domain.getStatus(),
                domain.getCreatedAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(savedResponse);
    }
}
