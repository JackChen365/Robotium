package quant.robotiumlibrary.waiter.impl;

import android.view.View;

import quant.robotiumlibrary.waiter.AbsViewChange;

/**
 * Created by Administrator on 2017/5/17.
 */

public class ViewSizeChange extends AbsViewChange {
    private final int width;
    private final int height;
    public ViewSizeChange(View view) {
        super(view);
        this.width=view.getWidth();
        this.height=view.getHeight();
    }

    @Override
    public boolean viewChanged() {
        return width!=view.getWidth()||height!=view.getHeight();
    }

}
