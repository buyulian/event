package com.me.event.http;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private HttpVersion httpVersion;
    private HttpCode code;
    private Map<String,String> header;
    private String body;

    public HttpResponse() {
        code=HttpCode.OK;
        httpVersion=HttpVersion.V11;
        header=new HashMap<>();
//        header.put("content-encoding","gzip");
        header.put("content-type","text/plain;charset=UTF-8");
        header.put("Accept-Ranges","bytes");
        header.put("Server","Microsoft-IIS/7.5");
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        header.put("Content-Length",String.valueOf(body.getBytes().length));
        this.body = body;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    public HttpCode getCode() {
        return code;
    }

    public void setCode(HttpCode code) {
        this.code = code;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }
}
