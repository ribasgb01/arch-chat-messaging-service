package com.microservice.archchatmessagingservice.application.exceptions;

public class NotAuthorizedToUnblockException extends RuntimeException{
    public NotAuthorizedToUnblockException(String message){
        super(message);
    }
}
