package edu.utdallas.cs4347.library.response;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

public class LibraryError {

    int errorCode = 0;
    
    String errorMessage = "";

    public LibraryError(int ec, String s) { 
        this.errorCode = ec;
        this.errorMessage = s;
    }

    public int getErrorCode() { 
        return this.errorCode;
    }

    public String getErrorMessage() { 
        return this.errorMessage;
    }

    public void setErrorCode(int ec) { 
        this.errorCode = ec;
    }

    public void setErrorMessage(String s) { 
        this.errorMessage = s;
    }
    
}