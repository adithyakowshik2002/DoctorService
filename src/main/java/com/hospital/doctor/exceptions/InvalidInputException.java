package com.hospital.doctor.exceptions;

public class InvalidInputException extends RuntimeException{
    public InvalidInputException(String msg){
        super(msg);
    }

}
