package quant.robotiumlibrary.event;

/**
 * Created by cz on 2017/3/17.
 */

public class EventResultItem {
    public static final String Void="void";

    public final String type;
    public final String desc;
    public final String value;

    public EventResultItem(String type,String desc, String value) {
        this.type = type;
        this.desc=desc;
        this.value = value;
    }

    public boolean isVoid(){
        return Void.equals(type);
    }
}
