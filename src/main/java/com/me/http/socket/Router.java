package com.me.http.socket;

import com.me.http.http.HttpMethod;
import com.me.http.http.HttpRequest;
import com.me.http.http.HttpResponse;
import com.me.event.pipe.Node;

import java.util.function.Function;

public interface Router {
    Function<Node<HttpRequest>,Node<HttpResponse>> route(String[] path, HttpMethod httpMethod);
}
