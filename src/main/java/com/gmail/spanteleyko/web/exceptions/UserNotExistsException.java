package com.gmail.spanteleyko.web.exceptions;

public class UserNotExistsException extends Exception{
    public UserNotExistsException(String errorMessage) {
        super(errorMessage);
    }
}
