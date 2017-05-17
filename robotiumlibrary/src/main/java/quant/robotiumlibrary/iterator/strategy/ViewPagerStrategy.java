package quant.robotiumlibrary.iterator.strategy;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import quant.robotiumlibrary.iterator.IteratorCallback;
import quant.robotiumlibrary.ISolo;

/**
 * Created by cz on 2017/4/7.
 */

public class ViewPagerStrategy implements ViewStrategic<ViewPager> {

    @Override
    public void process(IteratorCallback callback, ISolo solo, ViewPager source) {
        PagerAdapter adapter = source.getAdapter();
        if(null!=adapter){
            if(adapter instanceof FragmentPagerAdapter){
                //fragment操作对象

            } else if(adapter instanceof PagerAdapter){
                //控件操作对象

            }
        }
    }
}
