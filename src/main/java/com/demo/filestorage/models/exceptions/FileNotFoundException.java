package com.demo.filestorage.models.exceptions;

public class FileNotFoundException extends Exception{
    public FileNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
