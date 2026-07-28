package com.microservice.archchatmessagingservice.application.exceptions;

public class CannotAddSelfException extends RuntimeException{

    public CannotAddSelfException(String message){
        super(message);
    }
}

