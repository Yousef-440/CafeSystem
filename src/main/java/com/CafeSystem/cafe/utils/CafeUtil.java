package com.CafeSystem.cafe.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;

public class CafeUtil {

    public static ResponseEntity<String> getResponseEntity(String Message, HttpStatus httpStatus){
        return new ResponseEntity<>("{\"message\": \""+Message + "\"}",httpStatus);
    }
}