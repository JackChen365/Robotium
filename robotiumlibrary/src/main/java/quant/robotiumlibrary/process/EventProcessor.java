package quant.robotiumlibrary.process;

import java.util.ArrayList;
import java.util.List;

import quant.robotiumlibrary.event.EventItem;

/**
 * Created by cz on 2017/3/16.
 */

public class EventProcessor implements Processor {
    private final String TAG="EventProcessor";
    private final List<EventItem> eventItems;
    public EventProcessor() {
        this.eventItems=new ArrayList<>();
        //解析模板xml
    }

    @Override
    public synchronized void addEvent(EventItem item) {
        eventItems.add(item);
    }

    @Override
    public void process() {

    }
}
