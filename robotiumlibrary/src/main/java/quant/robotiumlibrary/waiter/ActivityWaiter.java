package quant.robotiumlibrary.waiter;

import android.app.Activity;
import android.os.SystemClock;

import quant.robotiumlibrary.ISolo;

/**
 * Created by czz on 2017/5/17.
 */
public class ActivityWaiter {
    private final ISolo solo;

    public ActivityWaiter(ISolo solo) {
        this.solo=solo;
    }

    public boolean waitActivityChanged(int timeout){
        boolean activityChanged = false;
        long currentTime = SystemClock.uptimeMillis();
        final long endTime = currentTime + timeout;
        final Activity currentActivity = solo.getCurrentActivity();
        while(currentTime < endTime){
            if(currentActivity!=solo.getCurrentActivity()){
                activityChanged=true;
                break;
            }
        }
        return activityChanged;
    }
}
