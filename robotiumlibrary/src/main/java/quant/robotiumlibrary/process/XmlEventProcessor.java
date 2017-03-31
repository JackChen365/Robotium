package quant.robotiumlibrary.process;

import android.text.TextUtils;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import quant.robotiumlibrary.event.EventItem;
import quant.robotiumlibrary.event.EventParamItem;
import quant.robotiumlibrary.event.EventResultItem;

/**
 * Created by cz on 2017/3/16.
 */

public class XmlEventProcessor implements Processor<XmlSerializer> {
    private final Map<String,List<EventItem>> eventItems;
    private final String EVENT="event";
    private final String METHOD_NAME="name";
    private final String METHOD_DESC="desc";
    private final String METHOD_INFO ="info";


    private final String RETURN_TYPE="r_type";
    private final String RETURN_DESC="r_desc";
    private final String RETURN_VALUE="r_value";

    private final String PARAM_NAME="p_name";
    private final String PARAM_DESC="p_desc";
    private final String PARAM_VALUE="p_value";

    private final String METHOD_PARAM="param";
    public XmlEventProcessor() {
        this.eventItems=new HashMap<>();
    }

    @Override
    public synchronized void addEvent(String tag,EventItem item) {
        List<EventItem> eventItems = this.eventItems.get(tag);
        if(null==eventItems){
            this.eventItems.put(tag,eventItems=new ArrayList<>());
        }
        eventItems.add(item);
    }

    @Override
    public void process(XmlSerializer xmlSerializer,String tag) {
        List<EventItem> eventItems = this.eventItems.get(tag);
        if(null!=eventItems){
            for(EventItem item:eventItems){
                try {
                    writeEventItem(xmlSerializer,item);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void writeEventItem(XmlSerializer xmlSerializer, EventItem item) throws IOException {
        xmlSerializer.startTag(null,EVENT);
        //方法信息
        xmlSerializer.attribute(null, METHOD_NAME, TextUtils.isEmpty(item.name)?"":item.name);
        xmlSerializer.attribute(null, METHOD_DESC, TextUtils.isEmpty(item.desc)?"":item.desc);
        xmlSerializer.attribute(null, METHOD_INFO, TextUtils.isEmpty(item.eventString)?"":item.eventString);
//        //方法返回值
        EventResultItem resultItem = item.resultItem;
        xmlSerializer.attribute(null, RETURN_TYPE, TextUtils.isEmpty(resultItem.type)?"":resultItem.type);
        xmlSerializer.attribute(null, RETURN_DESC, TextUtils.isEmpty(resultItem.desc)?"":resultItem.desc);
        xmlSerializer.attribute(null, RETURN_VALUE, TextUtils.isEmpty(resultItem.value)?"":resultItem.value);
//        //方法参数
        List<EventParamItem> paramItems = item.paramItems;
        for(EventParamItem param:paramItems){
            xmlSerializer.startTag(null,METHOD_PARAM);
            xmlSerializer.attribute(null, PARAM_NAME, TextUtils.isEmpty(param.name)?"":param.name);
            xmlSerializer.attribute(null, PARAM_DESC, TextUtils.isEmpty(param.desc)?"":param.desc);
            if(null!=param.value){
                xmlSerializer.attribute(null, PARAM_VALUE, String.valueOf(param.value));
            }
            xmlSerializer.endTag(null,METHOD_PARAM);
        }

        xmlSerializer.endTag(null,EVENT);
    }

}
