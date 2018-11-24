package com.me.event.loop;

public class EventBus {

    private static EventLoopGroup eventLoopGroup;

    private static Event initEvent;

    public static void start(){
        int availProcessors = Runtime.getRuntime().availableProcessors();
        start(availProcessors);
    }

    public static void start(int cpuNum){
        eventLoopGroup=new EventLoopGroup(cpuNum);
        eventLoopGroup.addEvent(initEvent);
        eventLoopGroup.start();
    }

    public static void sendEvent(Event event){
        eventLoopGroup.addEvent(event);
    }

    public static void setInitEvent(Event initEvent) {
        EventBus.initEvent = initEvent;
    }
}
