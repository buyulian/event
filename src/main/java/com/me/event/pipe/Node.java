package com.me.event.pipe;

import com.me.event.loop.Event;
import com.me.event.loop.EventBus;

import java.util.function.Consumer;
import java.util.function.Function;

public class Node <T> {

    private Node next;

    private Function<T,Boolean> eventFunction;

    public Node just(T t){
        Event event=()->{
            return eventFunction.apply(t);
        };
        EventBus.sendEvent(event);
        return this;
    }

    public void forEach(Consumer<?  super T> consumer){
        eventFunction=(t)->{
            consumer.accept(t);
            return true;
        };
    }

    public <R> Node<R> block(Function<? super T,? extends R> blockFunction){
        eventFunction=(t)->{
            Event event=()->{
                Wrap<R> wrap=new Wrap<>();
                Schedulers.elastic(()->{
                    R r = blockFunction.apply(t);
                    wrap.t=r;
                });

                Event callEvent=()->{
                    if(wrap.t==null){
                        return false;
                    }
                    sendNextEvent(wrap.t);
                    return true;
                };
                EventBus.sendEvent(callEvent);
                return true;
            };
            EventBus.sendEvent(event);
            return true;
        };
        next=new Node<R>();
        return next;
    }

    public <R> Node<R> map(Function<? super T,? extends R> mapFunction){
        eventFunction=(t)->{
            R r = mapFunction.apply(t);
            sendNextEvent(r);
            return true;
        };
        next=new Node<R>();
        return next;
    }

    private <R> void sendNextEvent(R r){
        if(next!=null){
            next.just(r);
        }
    }
}
