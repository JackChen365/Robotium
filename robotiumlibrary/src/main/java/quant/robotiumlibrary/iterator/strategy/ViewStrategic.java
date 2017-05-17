package quant.robotiumlibrary.iterator.strategy;

import android.view.View;

import quant.robotiumlibrary.iterator.IteratorCallback;
import quant.robotiumlibrary.ISolo;

/**
 * Created by cz on 2017/4/7.
 */
public interface ViewStrategic<V extends View>{
    void process(IteratorCallback callback, ISolo solo, V source);
}
