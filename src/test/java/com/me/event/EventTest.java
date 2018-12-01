package com.me.event;

import com.me.event.loop.EventBus;
import com.me.event.pipe.Node;
import com.me.event.pipe.Wrap;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class EventTest {

    private static Logger logger= LoggerFactory.getLogger(EventBus.class);

    @Test
    public void testEvent() {
        EventBus.init(1);
        EventBus.sendEvent(this::initEvent);
        EventBus.start();

    }

    private boolean initEvent() {
        Node<Integer> node = new Node<>();
        Node<Integer> node2 = new Node<>();

        Wrap<Integer> wrap=new Wrap<>();
        wrap.data =0;

        node.event(s->{
            String key="test-node-delay";
            Object value=EventBus.getMessage(key);
            Long start;
            if(value==null){
                start=System.currentTimeMillis();
                EventBus.setMessage(key,start);
            }else {
                start=(Long)value;
            }
            long now=System.currentTimeMillis();
            Long delay=10L;
            if(start+delay>now){
                return false;
            }else {
                node2.just(wrap.data++);

                EventBus.setMessage(key,now);
                return false;
            }
        });
        node2.filter(s->s%2==0)
                .map(s->s*10)
                .delay(10000)
                .flatMap(s->{
                    List<Integer> list=new LinkedList<>();
                    list.add(s+1);
                    list.add(s+2);
                    return list;
                })
                .subscribe(System.out::println);

        node.just(8);

        node=new Node<>();
        node.blockSubscribe((s)->{
            try {
                Thread.sleep(1000*12);
                logger.info("stop");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            EventBus.stop();
        });

        node.just(1);
        return true;
    }

}
