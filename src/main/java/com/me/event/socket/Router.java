package com.me.event.socket;

import com.me.event.http.HttpMethod;
import com.me.event.http.HttpRequest;
import com.me.event.http.HttpResponse;
import com.me.event.pipe.Node;

import java.util.function.Function;

public interface Router {
    Function<Node<HttpRequest>,Node<HttpResponse>> route(String[] path, HttpMethod httpMethod);
}
