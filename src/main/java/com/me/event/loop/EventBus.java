package com.me.event.loop;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventBus {

    private static EventLoopGroup eventLoopGroup;

    private static Map<String,Object> message=new ConcurrentHashMap<>();

    public static void start(){
        eventLoopGroup.start();
    }

    public static void init(){
        int availProcessors = Runtime.getRuntime().availableProcessors();
        eventLoopGroup=new EventLoopGroup(availProcessors);
    }

    public static void init(int cpuNum){
        eventLoopGroup=new EventLoopGroup(cpuNum);
    }

    public static void sendEvent(Event event){
        eventLoopGroup.addEvent(event);
    }

    public static void stop(){
        eventLoopGroup.stop();
    }

    public static Object getMessage(String key){
        return message.get(key);
    }

    public static void setMessage(String key,Object value){
        message.put(key,value);
    }

    public static void remove(String key){
        message.remove(key);
    }
}
