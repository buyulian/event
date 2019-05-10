package com.me.app.router;

import com.me.app.handler.HelloHandler;
import com.me.http.handler.SystemHandler;
import com.me.http.http.HttpMethod;
import com.me.http.http.HttpRequest;
import com.me.http.http.HttpResponse;
import com.me.event.pipe.Node;
import com.me.http.socket.Router;

import java.util.function.Function;

public class MainRouter implements Router {

    private HelloHandler helloHandler=new HelloHandler();

    private SystemHandler systemHandler=new SystemHandler();

    @Override
    public Function<Node<HttpRequest>,Node<HttpResponse>> route(String[] path, HttpMethod httpMethod){
        if(path==null||path.length==0){
            return helloHandler::hello;
        }
        if(path[0]==null||path[0].equals("")){
            return helloHandler::hello;
        }
        if(path[0].equals("static")){
            if(path.length==1){
                return systemHandler::notFound;
            }
            return systemHandler::staticFileRead;
        }
        if(path[0].equals("hello")){
            return helloHandler::hello;
        }
        return helloHandler::hello;
    }

}
