package quant.robotiumlibrary.waiter.impl;

import android.os.SystemClock;
import android.view.View;

import quant.robotiumlibrary.waiter.AbsViewChange;

/**
 * Created by Administrator on 2017/5/17.
 */

public class ViewVisibleChange extends AbsViewChange {
    private final int visibility;
    public ViewVisibleChange(View view) {
        super(view);
        this.visibility=view.getVisibility();
    }

    @Override
    public boolean viewChanged() {
        return visibility!=view.getVisibility();
    }
}
