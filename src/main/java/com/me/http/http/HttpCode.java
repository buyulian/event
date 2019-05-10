package com.me.http.http;

public enum HttpCode {
    OK("200","ok")
    ,BAD_REQUEST("400","Bad Request")
    ,UNAUTHORIZED("401","Unauthorized")
    ,NOT_FOUND("404","Forbidden")
    ,internal_server_error("500","Internal ConnectProcessor Error")
    ;
    private String code;
    private String text;

    HttpCode(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
