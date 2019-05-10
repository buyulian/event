package com.me.app.handler;

import com.me.http.http.HttpRequest;
import com.me.http.http.HttpResponse;
import com.me.event.pipe.Node;

public class HelloHandler {

    public Node<HttpResponse> hello(Node<HttpRequest> request){
        return request.map(httpRequest->{
            HttpResponse httpResponse=new HttpResponse();
            httpResponse.setBody("hello World");
            return httpResponse;
        });
    }
}
