package com.me.app;

import com.me.event.loop.EventBus;
import com.me.http.event.HttpServerEvent;
import com.me.app.router.MainRouter;

public class Main {
    public static void main(String[] args) {
        //设置线程数，默认为 cpu 核心数
        EventBus.init(1);
        //发送初始化路由事件
        EventBus.sendEvent(HttpServerEvent.routerEvent(new MainRouter()));
        //发送服务器启动事件
        EventBus.sendEvent(HttpServerEvent::initEvent);
        //启动
        EventBus.start();
    }
}
