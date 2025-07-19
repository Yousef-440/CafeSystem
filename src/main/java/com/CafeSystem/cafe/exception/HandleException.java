package com.CafeSystem.cafe.exception;

import org.springframework.http.HttpStatus;

public class HandleException extends RuntimeException{
    private final HttpStatus status;
    public HandleException(String message, HttpStatus status){
        super(message);
        this.status = status;
    }

}
