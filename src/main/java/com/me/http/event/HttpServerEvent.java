package com.me.http.event;

import com.me.event.loop.Event;
import com.me.event.pipe.Node;
import com.me.http.socket.HttpLinkRouterEvent;
import com.me.http.socket.HttpSocketHander;
import com.me.http.socket.Router;
import com.me.socket.ConnectProcessor;
import com.me.socket.SocketEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author liujiacun
 * @date 2019/5/10
 */
public class HttpServerEvent {

    private static final Logger logger = getLogger(HttpServerEvent.class);

    public static boolean initEvent(){
        ConnectProcessor.setSocketHandler(new HttpSocketHander());
        SocketEvent.initEvent();
        return true;
    }

    public static Event routerEvent(Router router){
        return ()->{
            HttpLinkRouterEvent.setRouter(router);
            return true;
        };
    }
}
