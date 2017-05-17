package quant.robotiumlibrary.waiter.impl;

import android.view.ViewGroup;

import quant.robotiumlibrary.waiter.AbsViewChange;

/**
 * Created by Administrator on 2017/5/17.
 */

public class ViewChildChange extends AbsViewChange<ViewGroup> {
    private final int childCount;
    public ViewChildChange(ViewGroup view) {
        super(view);
        this.childCount=view.getChildCount();
    }

    @Override
    public boolean viewChanged() {
        return childCount!=view.getChildCount();
    }
}
