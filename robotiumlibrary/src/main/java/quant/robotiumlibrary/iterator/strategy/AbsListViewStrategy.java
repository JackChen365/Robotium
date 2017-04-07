package quant.robotiumlibrary.iterator.strategy;

import android.widget.AbsListView;
import android.widget.ListAdapter;

import quant.robotiumlibrary.checker.ActivityChecker;
import quant.robotiumlibrary.iterator.IteratorCallback;
import quant.robotiumlibrary.solo.SoloInterface;

/**
 * Created by cz on 2017/4/7.
 */

public class AbsListViewStrategy implements ViewStrategic<AbsListView> {
    @Override
    public void process(IteratorCallback callback,SoloInterface solo, AbsListView source) {
        ListAdapter adapter = source.getAdapter();
        ActivityChecker activityChecker = callback.getActivityChecker();
        if(null!=adapter&&0<adapter.getCount()){
            for(int i=1;i<=adapter.getCount();i++){
                activityChecker.record();
                solo.clickInList(i);
                solo.scrollListToLine(source,i);
                //界面改变
                if(activityChecker.changed()){
                    solo.goBack();
                }
            }
        }
    }
}
