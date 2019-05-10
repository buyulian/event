package com.me.http.http;

public enum HttpVersion {
    V11("1.1")
    ;
    private String code;

    HttpVersion(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static HttpVersion getValue(String s){
        for (HttpVersion item : HttpVersion.values()) {
            if(item.getCode().endsWith(s)){
                return item;
            }
        }
        return null;
    }
}
