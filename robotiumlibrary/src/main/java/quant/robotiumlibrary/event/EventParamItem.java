package quant.robotiumlibrary.event;

/**
 * Created by cz on 2017/3/16.
 */

public class EventParamItem {
    public final String name;
    public final String desc;
    public final Object value;

    public EventParamItem(String name,String desc, Object value) {
        this.name = name;
        this.desc=desc;
        this.value = value;
    }
}
