package com.me.app;

import com.me.app.router.MainRouter;
import com.me.event.loop.EventBus;
import com.me.event.server.ServerEvent;

public class Main {
    public static void main(String[] args) {
        //设置线程数，默认为 cpu 核心数
        EventBus.init(1);
        //发送服务器启动事件
        EventBus.sendEvent(ServerEvent::initEvent);
        //发送初始化路由事件
        EventBus.sendEvent(ServerEvent.routerEvent(new MainRouter()));
        //启动
        EventBus.start();
    }
}
