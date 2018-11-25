package com.me.event.server;

import com.me.event.loop.Event;
import com.me.event.pipe.Node;
import com.me.event.socket.Router;
import com.me.event.socket.SocketHandler;

public class ServerEvent {

    public static boolean initEvent(){
        Node<Integer> node = new Node<>();

        node.map(s->{
            Server.init();
            return s;
        }).event(s->{
            Server.loop();
            return false;
        });

        node.just(1);
        return true;
    }


    public static Event routerEvent(Router router){
        return ()->{
            SocketHandler.setRouter(router);
            return true;
        };
    }
}
