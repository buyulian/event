package com.me.event;

import com.me.event.loop.EventBus;
import com.me.event.pipe.Node;

public class Main {
    public static void main(String[] args) {

        EventBus.setInitEvent(()->{
            Node<String> node=new Node<>();
            node.map(s->s+"1")
                    .block(s->{
                        s=s+s;
                        try {
                            Thread.sleep(1*1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return s;
                    })
                    .forEach(s-> System.out.println(s));

            for(int i=0;i<100;i++){
                node.just(String.valueOf(i));
            }
            return true;
        });

        EventBus.start(1);
    }
}
