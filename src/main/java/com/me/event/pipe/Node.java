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

    private Function<Exception,Boolean> exceptionHandler;

    private boolean transmitException=true;


    /**
     * just 方法
     */
    private Function<T,Node<T>> just=t->{
        if(eventFunction==null){
            return this;
        }
        Event event=()->{
            try {
                return eventFunction.apply(t);
            } catch (Exception e) {
                if(exceptionHandler==null){
                    e.printStackTrace();
                    return true;
                }
                return exceptionHandler.apply(e);
            }
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
        Node<T> node=new Node<>();
        setNext(node);
        return node;
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
                    wrap.data =r;
                });

                Event callEvent=()->{
                    if(wrap.data ==null){
                        return false;
                    }
                    sendNextEvent(wrap.data);
                    return true;
                };
                EventBus.sendEvent(callEvent);
                return true;
            };
            EventBus.sendEvent(event);
            return true;
        };
        Node<R> node=new Node<>();
        setNext(node);
        return node;
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
        Node<T> node=new Node<>();
        setNext(node);
        return node;
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
            if(wrap.data ==null){
                start=System.currentTimeMillis();
                wrap.data =start;
            }else {
                start=wrap.data;
            }
            long now=System.currentTimeMillis();
            if(now<start+millisecond){
                return false;
            }
            sendNextEvent(t);
            return true;
        };
        Node<T> node=new Node<>();
        setNext(node);
        return node;
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
        Node<R> node=new Node<>();
        setNext(node);
        return node;
    }

    /**
     * execute，然后返回自身
     * @param consumer
     * @return
     */
    public Node<T> execute(Consumer<T> consumer){
        eventFunction=(t)->{
            consumer.accept(t);
            sendNextEvent(t);
            return true;
        };
        Node<T> node=new Node<>();
        setNext(node);
        return node;
    }

    /**
     * execute，然后返回自身
     * @param looper
     * @return
     */
    public Node<T> loop(Function<? super T,Boolean> looper){
        eventFunction=(t)->{
            Boolean finished = looper.apply(t);
            if(finished){
                sendNextEvent(t);
            }
            return finished;
        };
        Node<T> node=new Node<>();
        setNext(node);
        return node;
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
        Node<R> node=new Node<>();
        setNext(node);
        return node;
    }

    /**
     * 启动下一个节点
     * @param r
     * @param <R>
     */
    @SuppressWarnings("unchecked")
    private <R> void sendNextEvent(R r){
        if(next!=null){
            if(next.transmitException){
                next.setExceptionHandler(exceptionHandler);
            }
            next.just(r);
        }
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node<T> setExceptionHandler(Function<Exception, Boolean> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        transmitException=false;
        return this;
    }

    public Node<T> transmitException(boolean transmitException) {
        this.transmitException = transmitException;
        return this;
    }

    public boolean isTransmitException() {
        return transmitException;
    }
}
