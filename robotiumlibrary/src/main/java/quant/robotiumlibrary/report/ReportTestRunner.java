package quant.robotiumlibrary.report;

import android.os.Bundle;
import android.os.Environment;
import android.test.AndroidTestRunner;
import android.test.InstrumentationTestRunner;
import android.util.Log;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

import quant.robotiumlibrary.file.FilePrefs;

/**
 * Test runner that should produce JUnit-compatible test results. It can be used
 * to produce output that is parseable by any tool that understands JUnit XML
 * output format. It is extremely useful for example when using CI systems (all
 * of which understand JUnit output XML) such as Jenkins, Hudson, Bamboo,
 * CruiseControl and many more.
 * 
 * The runner is flexible enough to produce separate file for each package,
 * class or for the whole test execution. By default it produces results split
 * by package.
 * 
 * Therefore you can run the runner with some extra parameters, like:
 * <p>
 * 
 * <code>
 * adb shell am instrument -e junitSplitLevel class -w somepackage/pl.polidea.instrumentation.PolideaInstrumentationTestRunner
 * </code>
 * </p>
 * It supports the following parameters (none of the parameters is mandatory,
 * they assume reasonable default values):
 * 
 * <ul>
 * <li>junitXmlOutput - boolean ("true"/"false") indicating whether XML Junit
 * output should be produced at all. Default is true</li>
 * <li>junitOutputDirectory - string specifying in which directory the XML files
 * should be placed. Be careful when setting this parameter. TestRunner deletes
 * all the files matching postfix and single filename before running from this
 * directory. Default is the on-device local "files" directory for the TESTED
 * application (not TESTING application!). Usually it is
 * <code>/data/data/&lt;package&gt;/files</code></li>
 * <li>junitOutputFilePostfix - string specifying what is the postfix of files
 * created. Default value is "-TEST.xml". The files are always prefixed with
 * package name with the exception of top-level, root package.</li>
 * <li>junitNoPackagePrefix - string specifying what is the prefix in case test
 * is in top-level directory (i.e. has no package). Default value is
 * "NO_PACKAGE".</li>
 * <li>junitSplitLevel - string specifying what splitting will be applied. The
 * runner can split the test results into several files: either per class,
 * package or it can produce a single big file for all tests run. Allowed value
 * are "class", "package" or "none". Default value is "package".</li>
 * <li>junitSingleFileName - string specifying what name will be given to output
 * file in case the split is "none". Default value is ALL-TEST.xml</li>
 * </ul>
 * 
 * For more details about parameters, visit <a
 * href="http://developer.android.com/guide/developing/testing/testing_otheride
 * .html#RunTestsCommand"> Android test runner command line documentation</a>
 * 
 * @author potiuk
 * 
 */
public class ReportTestRunner extends InstrumentationTestRunner {

    private static final String TAG = ReportTestRunner.class.getSimpleName();
    private static final String DEFAULT_NO_PACKAGE_PREFIX = "no_package";
    private static final String DEFAULT_SINGLE_FILE_NAME = "all-test.xml";
    private String junitOutputDirectory = null;
    private String junitNoPackagePrefix;
    private String junitSingleFileName;

    private boolean junitOutputEnabled;
    private boolean justCount;
    private final LinkedHashMap<Package, TestCaseInfo> caseMap = new LinkedHashMap<>();
    private boolean outputEnabled;
    private IReportProcessor reportProcessor;
    private TestCase currentTestCase;
    private boolean logOnly;

    /**
     * Stores information about single test run.
     * 
     */
    public static class TestInfo {
        public Package thePackage;
        public Class< ? extends TestCase> testCase;
        public String name;
        public Throwable error;
        public AssertionFailedError failure;
        public long time;

        @Override
        public String toString() {
            return name + "[" + testCase.getClass() + "] <" + thePackage + ">. Time: " + time + " ms. E<" + error
                    + ">, F <" + failure + ">";
        }
    }

    /**
     * Stores information about particular test case class - containing all
     * tests for that class.
     * 
     */
    public static class TestCaseInfo {
        public Package thePackage;
        public Class< ? extends TestCase> testCaseClass;
        public Map<String, TestInfo> testMap = new LinkedHashMap<>();

    }

    /**
     * Listener for executing test cases. It has the following purposes:
     * measures time of execution for each test, stores errors and failures that
     * occur during test as well as it optimizes garbage collection of the test
     * - after test is finished it cleans up all the static variables of the
     * test case. The last one is pretty useful if many tests are executed.
     * 
     */
    private class JunitTestListener implements TestListener {

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


        @Override
        public void startTest(final Test test) {
            Log.d(TAG, "Starting test: " + test);
            if (test instanceof TestCase) {
                currentTestCase=(TestCase)test;
                Thread.currentThread().setContextClassLoader(test.getClass().getClassLoader());
                startTime.set(System.currentTimeMillis());
            }
        }

        @Override
        public void endTest(final Test t) {
            if (t instanceof TestCase) {
                final TestCase testCase = (TestCase) t;
                cleanup(testCase);
                /*
                 * Note! This is copied from InstrumentationCoreTestRunner in
                 * android code
                 * 
                 * Make sure all tests take at least MINIMUM_TIME to complete.
                 * If they don't, we wait a bit. The Cupcake Binder can't handle
                 * too many operations in a very short time, which causes
                 * headache for the CTS.
                 */
                final long timeTaken = System.currentTimeMillis() - startTime.get();
                getTestInfo(testCase).time = timeTaken;
                if (timeTaken < MINIMUM_TIME) {
                    try {
                        Thread.sleep(MINIMUM_TIME - timeTaken);
                    } catch (final InterruptedException ignored) {
                        // We don't care.
                    }
                }
            }
            Log.d(TAG, "Finished test: " + t);
        }

        @Override
        public void addError(final Test test, final Throwable t) {
            if (test instanceof TestCase) {
                getTestInfo((TestCase) test).error = t;
            }
        }

        @Override
        public void addFailure(final Test test, final AssertionFailedError f) {
            if (test instanceof TestCase) {
                getTestInfo((TestCase) test).failure = f;
            }
        }

        /**
         * Nulls all non-static reference fields in the given test class. This
         * method helps us with those test classes that don't have an explicit
         * tearDown() method. Normally the garbage collector should take care of
         * everything, but since JUnit keeps references to all test cases, a
         * little help might be a good idea.
         * 
         * Note! This is copied from InstrumentationCoreTestRunner in android
         * code
         */
        private void cleanup(final TestCase test) {
            Class< ? > clazz = test.getClass();

            while (clazz != TestCase.class) {
                final Field[] fields = clazz.getDeclaredFields();
                for (final Field field : fields) {
                    final Field f = field;
                    if (!f.getType().isPrimitive() && !Modifier.isStatic(f.getModifiers())) {
                        try {
                            f.setAccessible(true);
                            f.set(test, null);
                        } catch (final Exception ignored) {
                            // Nothing we can do about it.
                        }
                    }
                }

                clazz = clazz.getSuperclass();
            }
        }
    }

    private synchronized TestInfo getTestInfo(final TestCase testCase) {
        final Class< ? extends TestCase> clazz = testCase.getClass();
        final Package thePackage = clazz.getPackage();
        final String name = testCase.getName();
        StringBuilder sb = new StringBuilder();
        sb.append(thePackage).append(".").append(clazz.getSimpleName()).append(".").append(name);
        final String mapKey = sb.toString();
        TestCaseInfo caseInfo = caseMap.get(thePackage);
        if (caseInfo == null) {
            caseInfo = new TestCaseInfo();
            caseInfo.testCaseClass = testCase.getClass();
            caseInfo.thePackage = thePackage;
            caseMap.put(thePackage, caseInfo);
        }
        TestInfo ti = caseInfo.testMap.get(mapKey);
        if (ti == null) {
            ti = new TestInfo();
            ti.name = name;
            ti.testCase = testCase.getClass();
            ti.thePackage = thePackage;
            caseInfo.testMap.put(mapKey, ti);
        }
        return ti;
    }


    private void setDefaultParameters() {
        if (junitOutputDirectory == null) {
            junitOutputDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        if (junitNoPackagePrefix == null) {
            junitNoPackagePrefix = DEFAULT_NO_PACKAGE_PREFIX;
        }
        if (junitSingleFileName == null) {
            junitSingleFileName = DEFAULT_SINGLE_FILE_NAME;
        }
    }

    private boolean getBooleanArgument(final Bundle arguments, final String tag, final boolean defaultValue) {
        final String tagString = arguments.getString(tag);
        if (tagString == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(tagString);
    }

    @Override
    public void onCreate(final Bundle arguments) {
        Log.d(TAG, "Creating the Test Runner with arguments: " + arguments.keySet());
        if (arguments != null) {
            junitOutputEnabled = getBooleanArgument(arguments, "junitXmlOutput", true);
            junitOutputDirectory = arguments.getString("junitOutputDirectory");
            junitNoPackagePrefix = arguments.getString("junitNoPackagePrefix");
            junitSingleFileName = arguments.getString("junitSingleFileName");
            justCount = getBooleanArgument(arguments, "count", false);
            logOnly = getBooleanArgument(arguments, "log", false);
        }
        setDefaultParameters();
        logParameters();
        createDirectoryIfNotExist();
        super.onCreate(arguments);
    }

    private void logParameters() {
        Log.d(TAG, "Test runner is running with the following parameters:");
        Log.d(TAG, "junitOutputDirectory: " + junitOutputDirectory);
        Log.d(TAG, "junitNoPackagePrefix: " + junitNoPackagePrefix);
        Log.d(TAG, "junitSingleFileName: " + junitSingleFileName);
    }
    
    private boolean createDirectoryIfNotExist(){
    	boolean created = false;
    	Log.d(TAG, "Creating output directory if it does not exist");
    	File directory =  new File(junitOutputDirectory);
    	if (!directory.exists()){
    		created = directory.mkdirs();
    	}
    	Log.d(TAG, "Created directory? " + created );
    	return created;
    }

    @Override
    public void finish(final int resultCode, final Bundle results) {
        if (outputEnabled) {
            Log.d(TAG, "Post processing");
            processPackageLevelSplit();
        }
        super.finish(resultCode, results);
    }


    private void processPackageLevelSplit() {
        Log.d(TAG, "Packages: " + caseMap.size());
        for (final Package p : caseMap.keySet()) {
            Log.d(TAG, "Processing package " + p);
            try {
                final File f = FilePrefs.getReportFile();
                if(null==reportProcessor){
                    reportProcessor=new XmlReportProcessor();
                }
                reportProcessor.start(f);
                try {
                    final TestCaseInfo tc = caseMap.get(p);
                    reportProcessor.process(tc);
                } finally {
                    reportProcessor.end();
                }
            } catch (final IOException e) {
                Log.e(TAG, "Error: " + e, e);
            }
        }
    }

    public TestCase getCurrentTestCase() {
        return currentTestCase;
    }

    @Override
    public AndroidTestRunner getAndroidTestRunner() {
        Log.d(TAG, "Getting android test runner");
        AndroidTestRunner runner = super.getAndroidTestRunner();
        if (junitOutputEnabled && !justCount && !logOnly) {
            Log.d(TAG, "JUnit test output enabled");
            outputEnabled = true;
            runner.addTestListener(new JunitTestListener());
        } else {
            outputEnabled = false;
            Log.d(TAG, "JUnit test output disabled: [ junitOutputEnabled : " + junitOutputEnabled + ", justCount : "
                    + justCount + ", logOnly : " + logOnly + " ]");
        }
        return runner;
    }
}
