package com.me.http.socket;

import com.me.http.http.HttpMethod;
import com.me.http.http.HttpRequest;
import com.me.http.http.HttpResponse;
import com.me.event.pipe.Node;
import com.me.event.pipe.Wrap;

public class HttpLinkRouterEvent {

    private static Router router;

    public static Node<HttpResponse> handleSocket(Node<HttpRequest> request){
        return link(request);
    }

    public static void setRouter(Router router) {
        HttpLinkRouterEvent.router = router;
    }

    private static Node<HttpResponse> link(Node<HttpRequest> request) {
        Node<HttpRequest> node=new Node<>();
        Node<HttpResponse> responseNode=new Node<>();
        Wrap<Node<HttpResponse>> wrap=new Wrap<>();
        request.map(r->{
            setHandler(wrap,node,r);
            wrap.data.map(s->s);
            wrap.data.setNext(responseNode);
            request.setNext(node);
            return r;
        });

        return responseNode;
    }

    private static void setHandler(Wrap<Node<HttpResponse>> wrap,Node<HttpRequest> node,HttpRequest request){
        String[] path = request.getPath();
        HttpMethod httpMethod=request.getMethod();
        wrap.data =router.route(path,httpMethod).apply(node);
    }

}
