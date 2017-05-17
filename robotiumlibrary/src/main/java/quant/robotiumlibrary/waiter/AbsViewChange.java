package quant.robotiumlibrary.waiter;

import android.view.View;

/**
 * Created by Administrator on 2017/5/17.
 */

public abstract class AbsViewChange <V extends View>{
    protected final V view;

    public AbsViewChange(V view) {
        this.view = view;
    }

    public abstract boolean viewChanged();
}
