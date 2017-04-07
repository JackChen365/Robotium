package quant.robotiumlibrary.checker;

import quant.robotiumlibrary.solo.SoloInterface;

/**
 * Created by cz on 2017/4/7.
 */

public abstract class ElementChecker {
    public final SoloInterface solo;

    public ElementChecker(SoloInterface solo) {
        this.solo = solo;
    }

    public abstract void record();

    public abstract boolean changed();
}
