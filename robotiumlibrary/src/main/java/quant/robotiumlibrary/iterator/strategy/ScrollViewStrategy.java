package quant.robotiumlibrary.iterator.strategy;

import android.view.View;
import android.widget.ScrollView;

import com.robotium.solo.Solo;

import quant.robotiumlibrary.iterator.IteratorCallback;
import quant.robotiumlibrary.ISolo;

/**
 * Created by cz on 2017/4/7.
 */

public class ScrollViewStrategy implements ViewStrategic<ScrollView> {

    @Override
    public void process(IteratorCallback callback, ISolo solo, ScrollView source) {
        int childCount = source.getChildCount();
        for(int i=0;i<childCount;i++){
            View view = source.getChildAt(i);
            if (view.isClickable()) {
                solo.scrollViewToSide(view, Solo.DOWN);
            }
        }
    }
}
