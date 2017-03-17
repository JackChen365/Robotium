package quant.robotiumlibrary.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cz on 2017/3/15.
 */

public class EventItem {
    public final long ct;
    public final String name;
    public final String desc;
    public final List<EventParamItem> paramItems;
    public EventResultItem resultItem;
    public String eventString;

    public EventItem(String name,String desc,EventResultItem resultItem) {
        this.name = name;
        this.desc = desc;
        this.resultItem=resultItem;
        this.ct=System.currentTimeMillis();
        this.paramItems=new ArrayList<>();
    }
}
