package com.popdq.app.model;

/**
 * Created by Dang Luu on 03/08/2016.
 */
public class Result {
    private int code;
    private String message;


    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "code: " + code + " message: " + message;
    }
}
