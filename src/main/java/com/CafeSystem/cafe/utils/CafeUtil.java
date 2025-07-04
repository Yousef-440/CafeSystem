package com.CafeSystem.cafe.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CafeUtil {

    public static ResponseEntity<String> getResponseEntity(String Message, HttpStatus httpStatus){
        return new ResponseEntity<>("{\"message\": \""+Message + "\"}",httpStatus);
    }

}
