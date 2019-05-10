package com.me.socket;

import com.me.event.pipe.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketEvent {
    private static Logger logger= LoggerFactory.getLogger(SocketEvent.class);

    public static boolean initEvent(){
        Node<Integer> node = new Node<>();

        node.map(s->{
            ConnectProcessor.init();
            logger.info("app start is successful");
            return s;
        }).event(s->{
            ConnectProcessor.loop();
            return false;
        });

        node.just(1);
        return true;
    }
}
