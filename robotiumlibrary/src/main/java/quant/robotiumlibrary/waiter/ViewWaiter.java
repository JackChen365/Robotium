package quant.robotiumlibrary.waiter;

import android.graphics.Rect;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.robotium.solo.Condition;

import java.util.ArrayList;
import java.util.List;

import quant.robotiumlibrary.ISolo;
import quant.robotiumlibrary.waiter.impl.ViewChildChange;
import quant.robotiumlibrary.waiter.impl.ViewLocalChange;
import quant.robotiumlibrary.waiter.impl.ViewProgressChange;
import quant.robotiumlibrary.waiter.impl.ViewSizeChange;
import quant.robotiumlibrary.waiter.impl.ViewVisibleChange;

/**
 * Created by Administrator on 2017/5/17.
 */

public class ViewWaiter {
    //View变化标记
    public static final int VIEW_VISIBLE_CHANGED=0x01;//隐藏显示变化
    public static final int VIEW_CHILD_CHANGED=0x02;//子孩子变化
    public static final int VIEW_LOCAL_CHANGED =0x04;//位置变化
    public static final int VIEW_SIZE_CHANGED=0x08;//大小变化
    static final int VIEW_END_BIT=0x10;//结束位

    private final ISolo solo;

    public ViewWaiter(ISolo solo) {
        this.solo = solo;
    }

    public boolean waitViewVisibleChanged(View view,long timeout){
        final ViewVisibleChange visibleChange=new ViewVisibleChange(view);
        return viewChanged(()->visibleChange.viewChanged(),timeout);
    }

    public boolean waitViewChildChanged(ViewGroup parent,long timeout){
        final ViewChildChange childChange=new ViewChildChange(parent);
        return viewChanged(()->childChange.viewChanged(),timeout);
    }

    public boolean waitViewLocalChanged(View view,long timeout){
        final ViewLocalChange localChange=new ViewLocalChange(view);
        return viewChanged(()->localChange.viewChanged(),timeout);
    }

    public boolean waitViewSizeChanged(View view,long timeout){
        final ViewSizeChange sizeChange=new ViewSizeChange(view);
        return viewChanged(()->sizeChange.viewChanged(),timeout);
    }

    public boolean waitProgressChanged(ProgressBar progressBar,long timeout){
        final int progress=progressBar.getProgress();
        return viewChanged(()->progress!=progressBar.getProgress(),timeout);
    }

    public boolean waitListViewDataChanged(ListView listView, long timeout){
        return waitViewChildChanged(listView,timeout);
    }

    public boolean waitRecyclerViewDataChanged(ViewGroup recyclerView, long timeout){
        return waitViewChildChanged(recyclerView,timeout);
    }

    boolean viewChanged(Condition condition,long timeout){
        boolean viewChanged = false;
        long currentTime = SystemClock.uptimeMillis();
        final long endTime = currentTime + timeout;
        while(currentTime < endTime){
            if(viewChanged=condition.isSatisfied()) break;
        }
        return viewChanged;
    }

    public boolean waitViewChanged(View view,int flag,long timeout){
        final List<AbsViewChange> viewChangesItems=new ArrayList<>();
        if(0!=(flag&VIEW_VISIBLE_CHANGED)){
            viewChangesItems.add(new ViewVisibleChange(view));
        }
        if(0!=(flag&VIEW_CHILD_CHANGED)&&view instanceof ViewGroup){
            viewChangesItems.add(new ViewVisibleChange(view));
        }
        if(0!=(flag&VIEW_LOCAL_CHANGED)){
            viewChangesItems.add(new ViewLocalChange(view));
        }
        if(0!=(flag&VIEW_SIZE_CHANGED)){
            viewChangesItems.add(new ViewSizeChange(view));
        }
        return viewChanged(()->viewChangesItems.stream().map(item->item.viewChanged()).reduce((result,item)->result&=item).orElse(false),timeout);
    }
}
