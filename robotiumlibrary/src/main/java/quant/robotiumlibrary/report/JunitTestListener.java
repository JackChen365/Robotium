package quant.robotiumlibrary.report;

import android.util.Log;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.util.LinkedHashMap;

import quant.robotiumlibrary.model.TestCaseInfo;
import quant.robotiumlibrary.model.TestInfo;

/**
 * Listener for executing test cases. It has the following purposes:
 * measures time of execution for each test, stores errors and failures that
 * occur during test as well as it optimizes garbage collection of the test
 * - after test is finished it cleans up all the static variables of the
 * test case. The last one is pretty useful if many tests are executed.
 *
 */
public class JunitTestListener extends RunListener {
    private static final JunitTestRunListenerWrapper listenerWrapper=new JunitTestRunListenerWrapper();
    private static final String TAG="JunitTestListener";

    public static JunitTestRunListenerWrapper getListenerWrapper(){
        return listenerWrapper;
    }
    /**
     * The minimum time we expect a test to take.
     */
    private static final int MINIMUM_TIME = 100;
    /**
     * Just in case it ever happens that the tests are run in parallell
     * (maybe future junit version?) we make sure that measured time is
     * separate per each thread running the tests.
     */
    private final ThreadLocal<Long> startTime = new ThreadLocal<>();

    private final LinkedHashMap<Package, TestCaseInfo> testCaseMap;
    private Description currentDescription;

    public JunitTestListener() {
        super();
        listenerWrapper.set(this);
        this.testCaseMap =new LinkedHashMap();
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
        super.testRunStarted(description);
        Log.e(TAG, "testRunStarted:" + description);
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        super.testRunFinished(result);
        Log.e(TAG, "testRunFinished:" + result);
    }

    @Override
    public void testStarted(Description description) throws Exception {
        super.testStarted(description);
        Log.e(TAG, "testStarted:" + description);
        this.currentDescription=description;
        Thread.currentThread().setContextClassLoader(description.getClass().getClassLoader());
        startTime.set(System.currentTimeMillis());
    }

    @Override
    public void testFinished(Description description) throws Exception {
        super.testFinished(description);
        final long timeTaken = System.currentTimeMillis() - startTime.get();
        getTestInfo(description).time = timeTaken;
        if (timeTaken < MINIMUM_TIME) {
            try {
                Thread.sleep(MINIMUM_TIME - timeTaken);
            } catch (final InterruptedException ignored) {
                // We don't care.
            }
        }
        Log.e(TAG, "testFinished:" + description);
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        super.testFailure(failure);
        Log.e(TAG, "testFailure:" + failure);
        getTestInfo(failure.getDescription()).error = failure.getException();
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        super.testAssumptionFailure(failure);
        Log.e(TAG, "testAssumptionFailure:" + failure);
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        super.testIgnored(description);
        Log.e(TAG, "testIgnored:" + description);
    }

    private synchronized TestInfo getTestInfo(final Description description) {
        final Class<?> clazz = description.getTestClass();
        final Package thePackage = clazz.getPackage();
        final String name = description.getMethodName();
        StringBuilder sb = new StringBuilder();
        sb.append(thePackage).append(".").append(clazz.getSimpleName()).append(".").append(name);
        final String mapKey = sb.toString();
        TestCaseInfo caseInfo = testCaseMap.get(thePackage);
        if (caseInfo == null) {
            caseInfo = new TestCaseInfo();
            caseInfo.testCaseClass = clazz.getSimpleName();
            caseInfo.thePackage = thePackage;
            testCaseMap.put(thePackage, caseInfo);
        }
        TestInfo ti = caseInfo.testMap.get(mapKey);
        if (ti == null) {
            ti = new TestInfo();
            ti.name = name;
            ti.testCase = description.getClassName();
            ti.thePackage = thePackage;
            caseInfo.testMap.put(mapKey, ti);
        }
        return ti;
    }

    public LinkedHashMap<Package, TestCaseInfo> getTestCaseMap() {
        return testCaseMap;
    }

    public Description getCurrentDescription() {
        return currentDescription;
    }
}