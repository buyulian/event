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

                //是否完成事件
                if(!isFinished){
                    eventList.addLast(event);
                }
            }

            long interval;
            eventNum=eventList.size();
            if(eventNum<10){
                interval=50/(eventNum+1);
            }else {
                interval=0;
            }
            //事件运行间隔
            if(interval>0){
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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
