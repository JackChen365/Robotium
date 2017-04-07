package quant.robotiumlibrary.checker;

import android.app.Activity;
import android.view.View;
import android.view.Window;

import quant.robotiumlibrary.solo.SoloInterface;

/**
 * Created by cz on 2017/4/7.
 */

public class ActivityChecker extends ElementChecker{
    private Activity activity;

    public ActivityChecker(SoloInterface solo) {
        super(solo);
    }

    @Override
    public void record() {
        activity=solo.getCurrentActivity();
    }

    @Override
    public boolean changed() {
        return solo.getCurrentActivity()==activity;
    }

    public boolean isFinished(){
        Window window = activity.getWindow();
        View decorView = window.getDecorView();
        return false;
    }
}
