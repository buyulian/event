package com.me.event.http;

import java.util.HashMap;

public class HttpAnalyze {

    public static HttpRequest getHttpRequest(String text){
        HttpRequest httpRequest=new HttpRequest();
        String[] lines = text.split("\r\n");
        String[] requestLine = lines[0].split(" ");
        httpRequest.setMethod(HttpMethod.getMethod(requestLine[0]));
        httpRequest.setUrl(requestLine[1]);
        httpRequest.setPath(requestLine[1].substring(1).split("/"));
        httpRequest.setHttpVersion(HttpVersion.getValue(requestLine[2].substring(5)));

        int i;
        HashMap<String, String> header = new HashMap<>();
        for(i=1;i<lines.length;i++){
            if (lines[i].equals("")) {
                break;
            }
            String[] rows = lines[i].split(": ");
            header.put(rows[0],rows[1]);
        }
        httpRequest.setHeader(header);

        if(httpRequest.getMethod().equals(HttpMethod.GET)||httpRequest.getMethod().equals(HttpMethod.DELETE)){
            return httpRequest;
        }

        StringBuilder sb=new StringBuilder();
        for (String line : lines) {
            sb.append(line);
        }

        httpRequest.setBody(sb.toString());

        return httpRequest;
    }

    public static String getTextFromHttpResponse(HttpResponse httpResponse){
        StringBuilder sb=new StringBuilder();
        sb.append("http/")
                .append(httpResponse.getHttpVersion().getCode())
                .append(" ")
                .append(httpResponse.getCode().getCode())
                .append(" ")
                .append(httpResponse.getCode().getText())
                .append("\r\n");
        httpResponse.getHeader().forEach((key,value)->{
            sb.append(key)
                    .append(": ")
                    .append(value)
                    .append("\r\n");
        });
        sb.append("\r\n");
        sb.append(httpResponse.getBody());
        return sb.toString();
    }

}
