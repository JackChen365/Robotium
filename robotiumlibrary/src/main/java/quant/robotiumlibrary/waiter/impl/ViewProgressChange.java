package quant.robotiumlibrary.waiter.impl;

import android.widget.ProgressBar;

import quant.robotiumlibrary.waiter.AbsViewChange;

/**
 * Created by Administrator on 2017/5/17.
 */

public class ViewProgressChange extends AbsViewChange<ProgressBar> {
    private final int progress;
    public ViewProgressChange(ProgressBar view) {
        super(view);
        this.progress=view.getProgress();
    }

    @Override
    public boolean viewChanged() {
        return progress!=view.getProgress();
    }
}
