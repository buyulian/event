package com.me.event.loop;

public class EventLoopGroup {

    private Thread[] eventLoopThrads;

    private EventLoop[] eventLoops;


    public EventLoopGroup(int eventLoopNum) {
        eventLoopThrads =new Thread[eventLoopNum-1];
        eventLoops=new EventLoop[eventLoopNum];

        for(int i=0;i<eventLoopNum;i++){
            eventLoops[i]=new EventLoop();
        }
    }

    public void start(){
        for(int i=1;i<eventLoops.length;i++){
            EventLoop eventLoop=eventLoops[i];
            eventLoopThrads[i-1]=new Thread(eventLoop::start);
            eventLoopThrads[i-1].start();
        }

        EventLoop eventLoopFirst=eventLoops[0];
        eventLoopFirst.start();
    }

    public void addEvent(Event event){
        int bestId=0;
        int bestValue=eventLoops[0].getBusyValue();
        for(int i=1;i<eventLoops.length;i++){
            int busyValue=eventLoops[i].getBusyValue();
            if(busyValue<bestValue){
                bestId=i;
                bestValue=busyValue;
            }
        }
        eventLoops[bestId].addEvent(event);
    }

    public void stop(){
        for(EventLoop eventLoop:eventLoops){
            eventLoop.stop();
        }
    }

}
