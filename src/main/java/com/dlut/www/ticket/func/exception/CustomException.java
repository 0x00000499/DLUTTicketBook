package com.dlut.www.ticket.func.exception;


import lombok.Data;

@Data
public class CustomException extends Exception{
    private String message;
    public CustomException(String message) {
        this.message = message;
    }
}
