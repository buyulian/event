package com.me.event.server;

import com.me.event.loop.Event;
import com.me.event.pipe.Node;
import com.me.event.socket.Router;
import com.me.event.socket.SocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerEvent {
    private static Logger logger= LoggerFactory.getLogger(ServerEvent.class);

    public static boolean initEvent(){
        Node<Integer> node = new Node<>();

        node.map(s->{
            Server.init();
            logger.info("server start is successful");
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
