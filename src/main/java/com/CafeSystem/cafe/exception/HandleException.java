package com.CafeSystem.cafe.exception;

import org.springframework.http.HttpStatus;

public class HandleException extends RuntimeException{

    public HandleException(String message){
        super(message);

    }

}
