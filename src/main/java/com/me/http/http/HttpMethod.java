package com.me.http.http;

public enum HttpMethod {
    GET,POST,PUT,DELETE;

    public static HttpMethod getMethod(String s){
        for (HttpMethod method : HttpMethod.values()) {
            if(method.toString().endsWith(s)){
                return method;
            }
        }
        return null;
    }
}
