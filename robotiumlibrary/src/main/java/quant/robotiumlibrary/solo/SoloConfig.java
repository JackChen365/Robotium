package quant.robotiumlibrary.solo;

import com.robotium.solo.Solo;

/**
 * Created by cz on 2017/4/7.
 */

public class SoloConfig extends Solo.Config {
    public static final int MIN_SLEEP_DURATION=100;


    public SoloConfig(){
        super();
        sleepMiniDuration=MIN_SLEEP_DURATION;
    }
}
