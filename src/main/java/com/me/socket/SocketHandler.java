package com.me.socket;

import java.io.IOException;
import java.nio.channels.SelectionKey;

/**
 * @author liujiacun
 * @date 2019/5/10
 */
public interface SocketHandler {

    void handleAccept(SelectionKey key) throws IOException;

    void handleRead(SelectionKey key) throws IOException;

    void handleWrite(SelectionKey key) throws IOException;
}
