package com.me.event.pipe;

import com.me.event.loop.Event;
import com.me.event.loop.EventBus;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 流程节点
 * @param <T>
 */
public class Node <T> {

    private Node next;

    /**
     * 事件函数
     */
    private Function<? super T,Boolean> eventFunction;


    /**
     * just 方法
     */
    private Function<T,Node<T>> just=t->{
        if(eventFunction==null){
            return this;
        }
        Event event=()->{
            return eventFunction.apply(t);
        };
        EventBus.sendEvent(event);
        return this;
    };

    /**
     * 发送数据
     * @param t
     * @return
     */
    public Node<T> just(T t){
        return just.apply(t);
    }

    /**
     * 设置 just 方法
     * @param just
     * @return
     */
    public Node<T> setJust(Function<T,Node<T>> just){
        this.just=just;
        return this;
    }

    /**
     * 自定义事件
     * @param eventFunction
     */
    public Node<T> event(Function<? super T,Boolean> eventFunction){
        this.eventFunction=eventFunction;
        next=new Node<T>();
        return next;
    }

    /**
     * 订阅
     * @param consumer
     */
    public void subscribe(Consumer<?  super T> consumer){
        eventFunction=(t)->{
            consumer.accept(t);
            return true;
        };
    }

    /**
     * 阻塞的订阅
     * @param consumer
     */
    public void blockSubscribe(Consumer<?  super T> consumer){
        blockSubscribe(consumer,ElasticScheduler.getInstance());
    }

    /**
     * 阻塞的订阅
     * @param consumer
     */
    public void blockSubscribe(Consumer<?  super T> consumer,Scheduler scheduler){
        eventFunction=(t)->{
            scheduler.execute(()->{
                consumer.accept(t);
            });
            return true;
        };
    }

    /**
     * 封装一个阻塞调用
     * @param mapper
     * @param <R>
     * @return
     */
    public <R> Node<R> block(Function<? super T,? extends R> mapper){
        return block(mapper,ElasticScheduler.getInstance());
    }

    /**
     * 封装一个阻塞调用
     * @param mapper
     * @param <R>
     * @return
     */
    public <R> Node<R> block(Function<? super T,? extends R> mapper,Scheduler scheduler){
        eventFunction=(t)->{
            Event event=()->{
                Wrap<R> wrap=new Wrap<>();
                scheduler.execute(()->{
                    R r = mapper.apply(t);
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

    /**
     * 过滤流
     * @param mapper
     * @return
     */
    public Node<T> filter(Function<? super T,Boolean> mapper){
        eventFunction=(t)->{
            Boolean isMatch = mapper.apply(t);
            if(isMatch){
                sendNextEvent(t);
            }
            return true;
        };
        next=new Node<T>();
        return next;
    }

    /**
     * 过滤流
     * @param millisecond
     * @return
     */
    public Node<T> delay(long millisecond){
        Wrap<Long> wrap=new Wrap<>();
        eventFunction=(t)->{
            Long start;
            if(wrap.t==null){
                start=System.currentTimeMillis();
                wrap.t=start;
            }else {
                start=wrap.t;
            }
            long now=System.currentTimeMillis();
            if(now<start+millisecond){
                return false;
            }
            sendNextEvent(t);
            return true;
        };
        next=new Node<T>();
        return next;
    }

    /**
     * map
     * @param mapper
     * @param <R>
     * @return
     */
    public <R> Node<R> map(Function<? super T,? extends R> mapper){
        eventFunction=(t)->{
            R r = mapper.apply(t);
            sendNextEvent(r);
            return true;
        };
        next=new Node<R>();
        return next;
    }

    /**
     * flatMap,将一个变成一组
     * @param mapper
     * @param <R>
     * @return
     */
    public <R> Node<R> flatMap(Function<? super T,Collection<? extends R> > mapper){
        eventFunction=(t)->{
            Collection<? extends R> collection=mapper.apply(t);
            collection.forEach(this::sendNextEvent);
            return true;
        };
        next=new Node<R>();
        return next;
    }

    /**
     * 启动下一个节点
     * @param r
     * @param <R>
     */
    private <R> void sendNextEvent(R r){
        if(next!=null){
            next.just(r);
        }
    }

}
