package quant.robotiumlibrary.solo;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.robotium.solo.Solo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import quant.robotiumlibrary.R;
import quant.robotiumlibrary.event.EventItem;
import quant.robotiumlibrary.event.EventParamItem;
import quant.robotiumlibrary.event.EventResultItem;
import quant.robotiumlibrary.process.EventProcessor;


/**
 * Created by cz on 2017/3/15.
 * 统计报告封装对象
 */

public final class NewSolo extends Solo implements SoloInterface {
    private static final String TAG="NewSolo";
    private static final SimpleDateFormat FORMATTER =new SimpleDateFormat("MM-dd HH:mm:dd");
    private static final SimpleDateFormat TAKE_SCREENSHOT_FORMATTER=new SimpleDateFormat("ddMMyy-hhmmss");
    private static final EventProcessor eventProcessor=new EventProcessor();
    //返回结果描述信息
    private static final String TYPE_VIEW="控件";
    private static final String TYPE_TEXT="文字";
    private static final String TYPE_LIST="列表";
    private static final String TYPE_BOOL="布尔";

    private static final String RESOURCE_ID="id";
    private static final String TAKE_SCREENSHOT="takeScreenshot";

    protected static final Properties properties=new Properties();

    private NewSolo(Instrumentation instrumentation, Activity activity) {
        super(instrumentation, activity);
        loadProperty(instrumentation);
    }

    private NewSolo(Instrumentation instrumentation, Config config) {
        super(instrumentation, config);
        loadProperty(instrumentation);
    }

    private NewSolo(Instrumentation instrumentation, Config config, Activity activity) {
        super(instrumentation, config, activity);
        loadProperty(instrumentation);
    }

    private NewSolo(Instrumentation instrumentation) {
        super(instrumentation);
        loadProperty(instrumentation);
    }

    /**
     * 装载属性映射列表
     * @param instrumentation
     */
    private void loadProperty(Instrumentation instrumentation) {
        InputStreamReader resourceAsStream = null;
        try {
            InputStream inputStream = instrumentation.getContext().getResources().openRawResource(R.raw.test);
            if(null!=inputStream){
                resourceAsStream = new InputStreamReader(inputStream,"utf-8");
                properties.load(resourceAsStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null!=resourceAsStream){
                try {
                    resourceAsStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static SoloInterface create(Instrumentation instrumentation, Activity activity){
        return newProxyInstance(new NewSolo(instrumentation,activity));
    }

    public static SoloInterface create(Instrumentation instrumentation, Config config){
        return newProxyInstance(new NewSolo(instrumentation,config));
    }

    public static SoloInterface create(Instrumentation instrumentation, Config config, Activity activity){
        return newProxyInstance(new NewSolo(instrumentation,config,activity));
    }

    public static SoloInterface create(Instrumentation instrumentation){
        return newProxyInstance(new NewSolo(instrumentation));
    }

    private static SoloInterface newProxyInstance(final NewSolo solo){
        return (SoloInterface) Proxy.newProxyInstance(solo.getClass().getClassLoader(), solo.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                //方法名
                String methodName = method.getName();
                //截图,空参数时,需要设定自定义的截图名称
                if(TAKE_SCREENSHOT.equalsIgnoreCase(methodName)&&null==args){
                    //无名称,生成名称
                    args=new Object[]{TAKE_SCREENSHOT_FORMATTER.format(new Date())+".jpg"};
                }
                Object invoke = method.invoke(solo, args);

                String methodNameDesc=properties.getProperty(methodName);
                //处理参数集
                List<EventParamItem> methodParamsItems = getMethodParamsItems(method, invoke, args);
                if("searchText".equalsIgnoreCase(methodName)){
                    System.out.println();
                }
                //处理返回值
                EventResultItem methodResultItem = getMethodResultItem(method, invoke);
                //生成参数条目
                EventItem eventItem=new EventItem(methodName,methodNameDesc,methodResultItem);
                eventItem.paramItems.addAll(methodParamsItems);
                //生成描述信息
                eventItem.eventString=getEventString(eventItem);
                Log.e(TAG,eventItem.eventString);
                eventProcessor.addEvent(eventItem);
                return invoke;
            }
        });
    }

    private static String getEventString(EventItem eventItem) {
        StringBuilder out=new StringBuilder();
//      03-17 18:02:42[inputText] 输入文字(位置:0 ,文字:Note 1 ) 返回结果
        out.append(FORMATTER.format(new Date())+ "["+eventItem.name+"] "+ eventItem.desc);
        List<EventParamItem> paramItems = eventItem.paramItems;
        if(!paramItems.isEmpty()){
            out.append(" (");
            for(EventParamItem item:paramItems){
                out.append(item.desc+":"+item.value+",");
            }
            out.deleteCharAt(out.length()-1);
            out.append(")");
        }
        EventResultItem resultItem = eventItem.resultItem;
        if(!resultItem.isVoid()){
            out.append("返回"+resultItem.desc+":"+resultItem.value);
        }
        return out.toString();
    }

    private static EventResultItem getMethodResultItem(Method method, Object invoke) {
        EventResultItem resultItem;
        Class<?> returnType = method.getReturnType();
        String type = returnType.getSimpleName();
        if(View.class==returnType){
            resultItem=new EventResultItem(type,TYPE_VIEW,(null==invoke?null:invoke.getClass().getName()));
        } else if(Boolean.class==returnType||boolean.class==returnType){
            resultItem=new EventResultItem(type,TYPE_BOOL,invoke.toString());
        } else if(List.class==returnType|| ArrayList.class==returnType){
            int size=null==invoke?0:((List)invoke).size();
            resultItem=new EventResultItem(type,TYPE_LIST,String.valueOf(size));
        } else if(String.class==returnType){
            resultItem=new EventResultItem(type,TYPE_TEXT,null==invoke?null:invoke.toString());
        } else {
            resultItem=new EventResultItem(type,null,null);
        }
        return resultItem;
    }


    private static List<EventParamItem> getMethodParamsItems(Method method, Object invoke, Object[] args){
        List<EventParamItem> eventParamItems=new ArrayList<>();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for(int i=0;i<parameterAnnotations.length;i++){
            Object arg = args[i];
            Annotation[] annotations = parameterAnnotations[i];
            for(Annotation annotation:annotations){
                if(annotation instanceof Param){
                    Param param= (Param) annotation;
                    String value = param.value();
                    if(!TextUtils.isEmpty(value)){
                        Object argValue=arg;
                        if(arg instanceof View){
                            //处理参数控件
                            View target= (View) arg;
                            argValue = getViewInfo(target);
                        } else if(RESOURCE_ID.equalsIgnoreCase(value)&&arg instanceof Integer){
                            //处理id
                            Integer resourceId = (Integer) arg;
                            if(invoke instanceof View){
                                View target= (View) invoke;
                                Resources resources = target.getResources();
                                if(null!=resources){
                                    String resourceName = resources.getResourceName(resourceId);
                                    if(!TextUtils.isEmpty(resourceName)){
                                        argValue=resourceName;
                                    }
                                }
                            } else {
                                argValue=Integer.toHexString(resourceId);
                            }
                        }
                        eventParamItems.add(new EventParamItem(value,properties.getProperty(value),argValue));
                    }
                }
            }
        }
        return eventParamItems;
    }

    private static String getViewInfo(View target) {
        String result=null;
        String simpleName = target.getClass().getSimpleName();
        int resourceId = target.getId();
        if(View.NO_ID!=resourceId){
            String resourceName = target.getResources().getResourceName(resourceId);
            result=simpleName+"/"+resourceName;
        }
        return result;
    }


}
