package quant.robotiumlibrary.waiter;

import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.annimon.stream.Stream;
import com.robotium.solo.Condition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/17.
 */

public class ViewWaiter {
    //View变化标记
    public static final int VIEW_VISIBLE_CHANGED=0x01;//隐藏显示变化
    public static final int VIEW_CHILD_CHANGED=0x02;//子孩子变化
    public static final int VIEW_LOCAL_CHANGED =0x04;//位置变化
    public static final int VIEW_SIZE_CHANGED=0x08;//大小变化

    public ViewWaiter() {
    }

    public boolean waitViewVisibleChanged(View view,long timeout){
        int visible=view.getVisibility();
        return viewChanged(() -> visible!=view.getVisibility(), timeout);
    }

    public boolean waitViewChildChanged(ViewGroup parent,long timeout){
        final int childCount=parent.getChildCount();
        return viewChanged(() -> childCount!=parent.getChildCount(), timeout);
    }

    public boolean waitViewLocalChanged(View view,long timeout){
        final float x=view.getX();
        final float y=view.getY();
        return viewChanged(() -> x!=view.getX()||y!=view.getY(), timeout);
    }

    public boolean waitViewSizeChanged(View view,long timeout){
        final int width = view.getWidth();
        final int height=view.getHeight();
        return viewChanged(() -> width!=view.getWidth()||height!=view.getHeight(), timeout);
    }

    public boolean waitProgressChanged(final ProgressBar progressBar,final int progress,long timeout){
        return viewChanged(() -> progress>=progressBar.getProgress(), timeout);
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

    public boolean waitViewChangedAndFlag(View view,int flag,long timeout){
        final List<Condition> viewChangesItems=getConditionByFlag(view,flag);
        return viewChanged(()-> Stream.of(viewChangesItems).map(item->item.isSatisfied()).reduce((result, item)->result&=item).orElse(false),timeout);
    }

    public boolean waitViewChangedOrFlag(View view,int flag,long timeout){
        final List<Condition> viewChangesItems=getConditionByFlag(view,flag);
        return viewChanged(()-> Stream.of(viewChangesItems).map(item->item.isSatisfied()).reduce((result, item)->result|=item).orElse(false),timeout);
    }

    List<Condition> getConditionByFlag(View view,int flag){
        final List<Condition> viewChangesItems=new ArrayList<>();
        if(0!=(flag&VIEW_VISIBLE_CHANGED)){
            int visible=view.getVisibility();
            viewChangesItems.add(() -> visible!=view.getVisibility());
        }
        if(0!=(flag&VIEW_CHILD_CHANGED)&&view instanceof ViewGroup){
            ViewGroup parent= (ViewGroup) view;
            final int childCount=parent.getChildCount();
            viewChangesItems.add(()->childCount!=parent.getChildCount());
        }
        if(0!=(flag&VIEW_LOCAL_CHANGED)){
            final float x=view.getX();
            final float y=view.getY();
            viewChangesItems.add(() -> x!=view.getX()||y!=view.getY());
        }
        if(0!=(flag&VIEW_SIZE_CHANGED)){
            final int width = view.getWidth();
            final int height=view.getHeight();
            viewChangesItems.add(() -> width!=view.getWidth()||height!=view.getHeight());
        }
        return viewChangesItems;
    }
}
