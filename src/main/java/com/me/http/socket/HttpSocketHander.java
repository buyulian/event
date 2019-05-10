package com.me.http.socket;

import com.me.event.pipe.Node;
import com.me.http.http.HttpAnalyze;
import com.me.http.http.HttpCode;
import com.me.http.http.HttpRequest;
import com.me.http.http.HttpResponse;
import com.me.socket.SocketHandler;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author liujiacun
 * @date 2019/5/10
 */
public class HttpSocketHander implements SocketHandler {

    private static final Logger logger = getLogger(HttpSocketHander.class);

    private static final int BUF_SIZE = 1024;

    @Override
    public void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
        SocketChannel sc = ssChannel.accept();
        sc.configureBlocking(false);
        sc.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocateDirect(BUF_SIZE));
        logger.info("accept");

    }

    @Override
    public void handleRead(SelectionKey key) throws IOException {
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

        //接续路由节点
        Node<HttpResponse> middleNode = HttpLinkRouterEvent.handleSocket(nextNode);

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

    @Override
    public void handleWrite(SelectionKey key) throws IOException {
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
    }
}
