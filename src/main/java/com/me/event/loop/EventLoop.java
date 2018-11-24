package com.me.event.loop;

import java.util.LinkedList;

public class EventLoop {

    private LinkedList<Event> eventList=new LinkedList<>();

    private volatile boolean isStop =false;

    private volatile int eventNum;

    public void start(){
        while (true){
            if (eventList.size()>0){
                Event event = eventList.pop();
                boolean isFinished = true;
                try {
                    isFinished = event.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(!isFinished){
                    eventList.addLast(event);
                }
            }
            eventNum=eventList.size();
            if(isStop){
                return;
            }
        }
    }

    public int getEventNum(){
        return eventNum;
    }

    public int getBusyValue(){
        return eventNum;
    }

    public void addEvent(Event event){
        eventList.add(event);
    }

    public void stop(){
        isStop =true;
    }
}
