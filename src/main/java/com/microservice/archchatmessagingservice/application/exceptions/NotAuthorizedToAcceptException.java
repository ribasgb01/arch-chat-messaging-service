package com.microservice.archchatmessagingservice.application.exceptions;

public class NotAuthorizedToAcceptException extends RuntimeException{
    public NotAuthorizedToAcceptException(String message){
        super(message);
    }
}
