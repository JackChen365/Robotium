package quant.robotiumlibrary.callback;

import android.view.View;

import java.util.Map;

import quant.robotiumlibrary.iterator.strategy.ViewStrategic;

/**
 * Created by cz on 2017/4/7.
 */

public interface ViewStrategyCallback {
    Map<Class<? extends View>,Class<? extends ViewStrategic>> getStrategyItems();
}
