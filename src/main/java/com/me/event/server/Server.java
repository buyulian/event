package com.me.event.server;

import com.me.event.http.HttpAnalyze;
import com.me.event.http.HttpCode;
import com.me.event.http.HttpRequest;
import com.me.event.http.HttpResponse;
import com.me.event.socket.SocketHandler;
import com.me.event.pipe.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class Server {

    private static Logger logger= LoggerFactory.getLogger(Server.class);

    private static final int BUF_SIZE = 1024;
    private static final int PORT = 8080;


    private static Selector selector;

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
                    handleAccept(key);
                }
                if (key.isReadable()) {
                    handleRead(key);
                }
                if (key.isValid() && key.isWritable()) {
                    handleWrite(key);
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

    private static void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
        SocketChannel sc = ssChannel.accept();
        sc.configureBlocking(false);
        sc.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocateDirect(BUF_SIZE));
        logger.info("accept");

    }

    private static void handleRead(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer buf = ByteBuffer.allocateDirect(BUF_SIZE);
        StringBuilder sb=new StringBuilder();
        while (sc.read(buf) > 0) {
            buf.flip();
            while (buf.hasRemaining()){
                char c = (char) buf.get();
                sb.append(c);
            }
            buf.clear();
        }
        String request = sb.toString();
        if(request.equals("")){
            sc.close();
            logger.info("read null");
            return;
        }
        logger.info("read");
        Node<String> beginNode=new Node<>();

        Node<HttpRequest> nextNode = beginNode.setExceptionHandler(ee->{
            logger.error("500 ",ee);
            HttpResponse httpResponse=new HttpResponse();
            httpResponse.setBody("error");
            httpResponse.setCode(HttpCode.internal_server_error);
            try {
                sc.register(key.selector(), SelectionKey.OP_WRITE,httpResponse);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
            return true;
        }).map(HttpAnalyze::getHttpRequest);

        Node<HttpResponse> middleNode = SocketHandler.handleSocket(nextNode);

        middleNode.subscribe(response->{
            try {
                sc.register(key.selector(), SelectionKey.OP_WRITE,response);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
        });

        logger.debug(request);
        beginNode.just(request);

        sc.register(key.selector(), SelectionKey.OP_CONNECT);
    }

    private static void handleWrite(SelectionKey key) throws IOException {
        HttpResponse httpResponse=(HttpResponse)key.attachment();
        String str=HttpAnalyze.getTextFromHttpResponse(httpResponse);
        byte[] buffer = str.getBytes();
        ByteBuffer buf=ByteBuffer.wrap(buffer);
        SocketChannel sc = (SocketChannel) key.channel();
        while (buf.hasRemaining()) {
            sc.write(buf);
        }
        logger.info("write");
        logger.debug(str);
        buf.compact();
        sc.close();
//        sc.register(key.selector(), SelectionKey.OP_CONNECT);
    }


}
