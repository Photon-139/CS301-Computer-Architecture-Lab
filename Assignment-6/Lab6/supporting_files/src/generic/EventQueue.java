package generic;

import java.util.Comparator;
import java.util.PriorityQueue;

import processor.Clock;

public class EventQueue {

    PriorityQueue<Event> queue;

    public EventQueue(){
        queue = new PriorityQueue<Event>(new EventComparator());
    }

    public void addEvent(Event e){
        queue.add(e);
    }

    public void processEvents(){
        while(!queue.isEmpty() && queue.peek().getEventTime() <= Clock.getCurrentTime()){
            Event e = queue.poll();
            e.getProcessingElement().handleEvent(e);
        }
    }

    class EventComparator implements Comparator<Event>{
        @Override
        public int compare(Event x, Event y){
            if(x.getEventTime() < y.getEventTime()){
                return -1;
            }
            else if(x.getEventTime() > y.getEventTime()){
                return 1;
            }
            else{
                return 0;
            }
        }
    }
}
