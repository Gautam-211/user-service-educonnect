package com.example.userservice.userservice.exception;

public class RoleAccessDeniedException extends RuntimeException{
    public RoleAccessDeniedException(String message) {
        super(message);
    }
}
