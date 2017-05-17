package quant.robotiumlibrary.waiter.impl;

import android.view.View;

import quant.robotiumlibrary.waiter.AbsViewChange;

/**
 * Created by Administrator on 2017/5/17.
 */

public class ViewLocalChange extends AbsViewChange {
    private final float x;
    private final float y;
    public ViewLocalChange(View view) {
        super(view);
        this.x=view.getX();
        this.y=view.getY();
    }

    @Override
    public boolean viewChanged() {
        return this.x!=view.getX()|| this.y!=view.getY();
    }
}
