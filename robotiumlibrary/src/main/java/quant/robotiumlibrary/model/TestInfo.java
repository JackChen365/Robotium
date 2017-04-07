package quant.robotiumlibrary.model;

import junit.framework.AssertionFailedError;

/**
     * Stores information about single test run.
     * 
     */
public class TestInfo {
    public Package thePackage;
    public String testCase;
    public String name;
    public Throwable error;
    public AssertionFailedError failure;
    public long time;

    @Override
    public String toString() {
        return name + "[" + testCase + "] <" + thePackage + ">. Time: " + time + " ms. E<" + error
                + ">, F <" + failure + ">";
    }
}