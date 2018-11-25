package com.me.event.http;

import java.util.Map;

public class HttpRequest {
    private String body;
    private HttpMethod method;
    private HttpVersion httpVersion;
    private String url;
    private Map<String,String> header;
    private String[] path;
    private Map<String,String> parameter;
    private Map<String,String> postParameter;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public String[] getPath() {
        return path;
    }

    public void setPath(String[] path) {
        this.path = path;
    }

    public Map<String, String> getParameter() {
        return parameter;
    }

    public void setParameter(Map<String, String> parameter) {
        this.parameter = parameter;
    }

    public Map<String, String> getPostParameter() {
        return postParameter;
    }

    public void setPostParameter(Map<String, String> postParameter) {
        this.postParameter = postParameter;
    }
}
