package quant.robotiumlibrary.iterator.strategy;

import android.support.v7.widget.RecyclerView;

import quant.robotiumlibrary.iterator.IteratorCallback;
import quant.robotiumlibrary.ISolo;

/**
 * Created by cz on 2017/4/7.
 */

public class RecyclerViewStrategy implements ViewStrategic<RecyclerView> {

    @Override
    public void process(IteratorCallback callback, ISolo solo, RecyclerView source) {
        //点击recyclerView内每一个条目
        RecyclerView.Adapter adapter = source.getAdapter();
        if(null!=adapter&&0<adapter.getItemCount()){
            for(int i=0;i<adapter.getItemCount();i++){
                solo.clickInRecyclerView(i);
            }
        }
    }
}
