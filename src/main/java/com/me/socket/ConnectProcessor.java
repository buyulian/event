package com.me.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

public class ConnectProcessor {

    private static Logger logger= LoggerFactory.getLogger(ConnectProcessor.class);

    private static final int PORT = 8080;

    private static Selector selector;

    private static SocketHandler socketHandler;

    public static void init(){
        selector = null;
        ServerSocketChannel ssc = null;
        try {
            selector = Selector.open();
            ssc = ServerSocketChannel.open();
            ssc.socket().bind(new InetSocketAddress(PORT));
            ssc.configureBlocking(false);
            ssc.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (selector != null) {
                    selector.close();
                }
                if (ssc != null) {
                    ssc.close();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    public static void loop(){
        try {
            if (selector.selectNow() == 0) {
                return ;
            }
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                if (key.isAcceptable()) {
                    socketHandler.handleAccept(key);
                }
                if (key.isReadable()) {
                    socketHandler.handleRead(key);
                }
                if (key.isValid() && key.isWritable()) {
                    socketHandler.handleWrite(key);
                }
                if (key.isValid() && key.isConnectable()) {
                    logger.info("isConnectable = true");
                }
                iter.remove();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setSocketHandler(SocketHandler socketHandler) {
        ConnectProcessor.socketHandler = socketHandler;
    }
}
