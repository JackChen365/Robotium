package quant.robotiumlibrary.process;

import quant.robotiumlibrary.event.EventItem;

/**
 * Created by cz on 2017/3/16.
 */

public interface Processor<T> {
    void addEvent(String tag,EventItem item);
    void process(T t,String tag);
}
