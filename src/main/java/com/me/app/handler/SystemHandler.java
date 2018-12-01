package com.me.app.handler;

import com.me.event.http.HttpCode;
import com.me.event.http.HttpRequest;
import com.me.event.http.HttpResponse;
import com.me.event.pipe.Node;
import com.me.event.pipe.Wrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class SystemHandler {

    private static final Logger LOGGER= LoggerFactory.getLogger(SystemHandler.class);

    public Node<HttpResponse> notFound(Node<HttpRequest> request){
        return request.map(httpRequest->{
            HttpResponse httpResponse=new HttpResponse();
            httpResponse.setBody("404");
            httpResponse.setCode(HttpCode.NOT_FOUND);
            return httpResponse;
        });
    }

    public Node<HttpResponse> staticFileRead(Node<HttpRequest> request){
        Wrap<String> wrap=new Wrap<>();
        return request.execute(httpRequest->{
            String[] paths = httpRequest.getPath();
            String filePath=paths[1];
            try {
                AsynchronousFileChannel fileChannel =
                        AsynchronousFileChannel.open(Paths.get(filePath), StandardOpenOption.READ);
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                long position = 0;
                fileChannel.read(buffer,position,buffer,new CompletionHandler<Integer, ByteBuffer>() {

                    @Override
                    public void completed(Integer result, ByteBuffer buffer) {
                        buffer.flip();
                        byte[] data = new byte[buffer.limit()];
                        buffer.get(data);
                        wrap.data =new String(data);
                        wrap.code=1;
                        buffer.clear();
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        LOGGER.error("Read failed",exc);
                        exc.printStackTrace();
                    }
                });
            } catch (IOException e) {
                LOGGER.error("static file read io exception");
                return ;
            }
            return ;
        }).loop(httpRequest -> {
            if(wrap.code==0){
                return false;
            }
            return true;
        }).map(httpRequest -> {
            HttpResponse httpResponse=new HttpResponse();
            if(wrap.code!=1){
                httpResponse.setBody("404");
                httpResponse.setCode(HttpCode.NOT_FOUND);
                return httpResponse;
            }
            httpResponse.setCode(HttpCode.OK);
            httpResponse.setBody(wrap.data);
            return httpResponse;
        });
    }
}
