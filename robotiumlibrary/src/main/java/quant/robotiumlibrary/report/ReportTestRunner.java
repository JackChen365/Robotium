package quant.robotiumlibrary.report;

import android.os.Bundle;
import android.os.Environment;
import android.support.test.runner.AndroidJUnitRunner;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import quant.robotiumlibrary.file.FilePrefs;
import quant.robotiumlibrary.model.TestCaseInfo;

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
public class ReportTestRunner extends AndroidJUnitRunner {

    private static final String TAG = ReportTestRunner.class.getSimpleName();
    private static final String ARGUMENT_LISTENER = "listener";
    private static final String DEFAULT_NO_PACKAGE_PREFIX = "no_package";
    private static final String DEFAULT_SINGLE_FILE_NAME = "all-test.xml";
    private String junitOutputDirectory = null;
    private String junitNoPackagePrefix;
    private String junitSingleFileName;

    private final Map<String,Object> argumentParams=new HashMap<>();
    private IReportProcessor reportProcessor;

    @Override
    public void onCreate(final Bundle arguments) {
        if (arguments != null) {
            Set<String> argumentKeySet = arguments.keySet();
            Log.d(TAG, "Creating the Test Runner with arguments: " + argumentKeySet);
            for(String key:argumentKeySet){
                argumentParams.put(key,arguments.get(key));
            }
            junitOutputDirectory = arguments.getString("junitOutputDirectory");
            junitNoPackagePrefix = arguments.getString("junitNoPackagePrefix");
            junitSingleFileName = arguments.getString("junitSingleFileName");
        }
        setDefaultParameters(arguments);
        logParameters();
        createDirectoryIfNotExist();
        super.onCreate(arguments);
    }


    private void setDefaultParameters(Bundle arguments) {
        if (junitOutputDirectory == null) {
            junitOutputDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        if (junitNoPackagePrefix == null) {
            junitNoPackagePrefix = DEFAULT_NO_PACKAGE_PREFIX;
        }
        if (junitSingleFileName == null) {
            junitSingleFileName = DEFAULT_SINGLE_FILE_NAME;
        }
        String listenerClass = JunitTestListener.class.getName();
        String listenerValue = arguments.getString(ARGUMENT_LISTENER);
        if(TextUtils.isEmpty(listenerValue)){
            arguments.putString(ARGUMENT_LISTENER, listenerClass);
        } else {
            arguments.putString(ARGUMENT_LISTENER,listenerValue+","+listenerClass);
        }
    }

    private boolean getBooleanArgument(final Bundle arguments, final String tag, final boolean defaultValue) {
        final String tagString = arguments.getString(tag);
        if (tagString == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(tagString);
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
        Log.d(TAG, "Post processing");
        processPackageLevelSplit();
        super.finish(resultCode, results);
    }


    private void processPackageLevelSplit() {
        JunitTestRunListenerWrapper listenerWrapper = JunitTestListener.getListenerWrapper();
            JunitTestListener junitTestListener = listenerWrapper.get();
            if(null!=junitTestListener){
            LinkedHashMap<Package, TestCaseInfo> testCaseMap = junitTestListener.getTestCaseMap();
            Log.d(TAG, "Packages: " + testCaseMap.size());
            for (final Package p : testCaseMap.keySet()) {
                Log.d(TAG, "Processing package " + p);
                try {
                    final File f = FilePrefs.getReportFile();
                    if(null==reportProcessor){
                        reportProcessor=new XmlReportProcessor();
                    }
                    reportProcessor.start(f);
                    try {
                        final TestCaseInfo tc = testCaseMap.get(p);
                        reportProcessor.process(tc);
                    } finally {
                        reportProcessor.end();
                    }
                } catch (final IOException e) {
                    Log.e(TAG, "Error: " + e, e);
                }
            }
        }
    }

    public Map<String,Object> getArgumentParams(){
        return argumentParams;
    }

}
