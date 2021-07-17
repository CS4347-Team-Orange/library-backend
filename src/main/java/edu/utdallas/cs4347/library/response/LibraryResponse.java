package edu.utdallas.cs4347.library.response;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

public class LibraryResponse {

    Object data = "";

    String message = "success";

    int code = 0;

    public LibraryResponse() {

    }

    public LibraryResponse(String s) {
        this.message = s;
    }

    public LibraryResponse(int code, String s) {
        this.code = code;
        this.message = s;
    }

    public LibraryResponse(int code, String s, Object o) {
        this.code = code;
        this.message = s;
        this.data = o;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String s) {
        this.message = s;
    }

    public int getCode() { return this.code; }

    public void setCode(int code) { this.code = code; }

    public Object getData() { return this.data; }

    public void setData(Object data) { this.data = data; }
}