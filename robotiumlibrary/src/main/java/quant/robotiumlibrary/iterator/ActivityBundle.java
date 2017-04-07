package quant.robotiumlibrary.iterator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by cz on 2017/4/7.
 */

public class ActivityBundle {
    public final Class<? extends Activity> clazz;
    private final Map<String,Object> extras;
    private final String action;
    private final Uri uri;

    public ActivityBundle(Class<? extends Activity> clazz, Map<String,Object> extras,String action,Uri uri) {
        this.clazz = clazz;
        this.extras=new HashMap<>(extras);
        this.uri=uri;
        this.action=action;
    }

    public static ActivityBundle create(Activity activity){
        ActivityBundle activityBundle=null;
        if(null!=activity){
            Class<? extends Activity> clazz = activity.getClass();
            Map<String,Object> extras=new HashMap<>();
            Intent intent = activity.getIntent();
            Bundle bundle = intent.getExtras();
            if(null!=bundle){
                Set<String> keySet = bundle.keySet();
                for(String key:keySet){
                    extras.put(key,bundle.get(key));
                }
            }
            activityBundle=new ActivityBundle(clazz,extras,intent.getAction(),intent.getData());
        }
        return activityBundle;
    }

    public Intent getActivityIntent(Context context){
        Intent intent=new Intent(context,clazz);
        intent.setData(uri);
        intent.setAction(action);
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        for (Map.Entry<String, Object> entry : extras.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Integer) {
                intent.putExtra(key, Integer.valueOf(value.toString()));
            } else if (value instanceof String) {
                intent.putExtra(key, value.toString());
            } else if (value instanceof Double) {
                intent.putExtra(key, ((Double) value).doubleValue());
            } else if (value instanceof Float) {
                intent.putExtra(key, ((Float) value).floatValue());
            } else if (value instanceof Long) {
                intent.putExtra(key, ((Long) value).longValue());
            } else if (value instanceof Boolean) {
                intent.putExtra(key, ((Boolean) value).booleanValue());
            } else if (value instanceof Byte) {
                intent.putExtra(key, ((Byte) value).byteValue());
            } else if (value instanceof Short) {
                intent.putExtra(key, ((Short) value).shortValue());
            } else if (value instanceof Serializable) {
                intent.putExtra(key, ((Serializable) value));
            } else if (value instanceof Parcelable[]) {
                intent.putExtra(key, ((Parcelable[]) value));
            } else if (value instanceof Parcelable) {
                intent.putExtra(key, ((Parcelable) value));
            } else if (value instanceof CharSequence) {
                intent.putExtra(key, ((CharSequence) value));
            } else if (value instanceof Bundle) {
                intent.putExtra(key, ((Bundle) value));
            } else if (value instanceof CharSequence[]) {
                intent.putExtra(key, ((CharSequence[]) value));
            } else if (value instanceof String[]) {
                intent.putExtra(key, ((String[]) value));
            } else if (value instanceof double[]) {
                intent.putExtra(key, ((double[]) value));
            } else if (value instanceof float[]) {
                intent.putExtra(key, ((float[]) value));
            } else if (value instanceof long[]) {
                intent.putExtra(key, ((long[]) value));
            } else if (value instanceof int[]) {
                intent.putExtra(key, ((int[]) value));
            } else if (value instanceof char[]) {
                intent.putExtra(key, ((char[]) value));
            } else if (value instanceof short[]) {
                intent.putExtra(key, ((short[]) value));
            } else if (value instanceof byte[]) {
                intent.putExtra(key, ((byte[]) value));
            } else if (value instanceof boolean[]) {
                intent.putExtra(key, ((boolean[]) value));
            }
        }
        return intent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityBundle that = (ActivityBundle) o;
        boolean result=false;
        if(extras.size()==that.extras.size()&&!extras.isEmpty()){
            List<String> items=new ArrayList<>(extras.keySet());
            items.removeAll(that.extras.keySet());
            result=items.isEmpty();
        }
        return clazz==that.clazz&&result;
    }


}
