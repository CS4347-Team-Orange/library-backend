package edu.utdallas.cs4347.library.exception;

public class BookStateException extends Exception { 
    public BookStateException(String error) { 
        super(error);
    }
}