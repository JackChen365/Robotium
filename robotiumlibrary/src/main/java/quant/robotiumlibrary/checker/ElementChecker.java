package quant.robotiumlibrary.checker;

import quant.robotiumlibrary.ISolo;

/**
 * Created by cz on 2017/4/7.
 */

public abstract class ElementChecker {
    public final ISolo solo;

    public ElementChecker(ISolo solo) {
        this.solo = solo;
    }

    public abstract void record();

    public abstract boolean changed();
}
