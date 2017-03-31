package quant.robotiumlibrary.report;

import android.util.Log;
import android.util.Xml;

import junit.framework.TestCase;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

import quant.robotiumlibrary.process.Processor;
import quant.robotiumlibrary.solo.NewSolo;

import static android.content.ContentValues.TAG;

/**
 * Created by cz on 2017/3/20.
 */

public class XmlReportProcessor implements IReportProcessor {
    private static final String TESTSUITES = "testsuites";
    private static final String TESTSUITE = "testsuite";
    private static final String ERRORS = "errors";
    private static final String FAILURES = "failures";
    private static final String ERROR = "error";
    private static final String FAILURE = "failure";
    private static final String NAME = "name";
    private static final String VALUE = "value";
    private static final String PACKAGE = "package";
    private static final String TESTS = "tests";
    private static final String TESTCASE = "testcase";
    private static final String TEST_STEP = "teststep";
    private static final String CLASSNAME = "classname";
    private static final String TIME = "time";
    private static final String CREATE_TIME="ct";
    private static final String TIMESTAMP = "timestamp";
    private static final String SYSTEM_OUT = "system-out";
    private static final String SYSTEM_ERR = "system-err";

    private XmlSerializer currentXmlSerializer;
    private PrintWriter currentFileWriter;

    public XmlReportProcessor() {
    }

    @Override
    public void start(File outputFile) throws IOException{
        Log.d(TAG, "Writing to file " + outputFile);
        currentXmlSerializer = Xml.newSerializer();
        currentFileWriter = new PrintWriter(outputFile, "UTF-8");
        currentXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        currentXmlSerializer.setOutput(currentFileWriter);
        currentXmlSerializer.startDocument("UTF-8", null);
        currentXmlSerializer.startTag(null, TESTSUITES);
    }

    @Override
    public void process(ReportTestRunner.TestCaseInfo testCaseInfo) throws IOException{
        final Package thePackage = testCaseInfo.thePackage;
        final Class< ? extends TestCase> clazz = testCaseInfo.testCaseClass;
        final int tests = testCaseInfo.testMap.size();
        final String timestamp = getTimestamp();
        int errors = 0;
        int failures = 0;
        int time = 0;
        for (final ReportTestRunner.TestInfo testInfo : testCaseInfo.testMap.values()) {
            if (testInfo.error != null) {
                errors++;
            }
            if (testInfo.failure != null) {
                failures++;
            }
            time += testInfo.time;
        }
        currentXmlSerializer.startTag(null, TESTSUITE);
        currentXmlSerializer.attribute(null, ERRORS, Integer.toString(errors));
        currentXmlSerializer.attribute(null, FAILURES, Integer.toString(failures));
        currentXmlSerializer.attribute(null, NAME, clazz.getName());
        currentXmlSerializer.attribute(null, PACKAGE, thePackage == null ? "" : thePackage.getName());
        currentXmlSerializer.attribute(null, TESTS, Integer.toString(tests));
        currentXmlSerializer.attribute(null, TIME, Double.toString(time / 1000.0));
        currentXmlSerializer.attribute(null, TIMESTAMP, timestamp);
        currentXmlSerializer.attribute(null, CREATE_TIME,String.valueOf(System.currentTimeMillis()));
        for (final ReportTestRunner.TestInfo testInfo : testCaseInfo.testMap.values()) {
            writeTestInfo(testInfo);
        }
        currentXmlSerializer.startTag(null, SYSTEM_OUT);
        currentXmlSerializer.endTag(null, SYSTEM_OUT);
        currentXmlSerializer.startTag(null, SYSTEM_ERR);
        currentXmlSerializer.endTag(null, SYSTEM_ERR);
        currentXmlSerializer.endTag(null, TESTSUITE);
    }

    @Override
    public void end() throws IOException{
        Log.d(TAG, "closing file");
        currentXmlSerializer.endTag(null, TESTSUITES);
        currentXmlSerializer.endDocument();
        currentFileWriter.flush();
        currentFileWriter.close();
    }

    private String getTimestamp() {
        final long time = System.currentTimeMillis();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return sdf.format(time);
    }


    private void writeTestInfo(final ReportTestRunner.TestInfo testInfo) throws IllegalArgumentException, IllegalStateException,
            IOException {
        currentXmlSerializer.startTag(null, TESTCASE);
        currentXmlSerializer.attribute(null, CLASSNAME, testInfo.testCase.getName());
        currentXmlSerializer.attribute(null, NAME, testInfo.name);
        currentXmlSerializer.attribute(null, TIME, Double.toString(testInfo.time / 1000.0));
        if (testInfo.error != null) {
            currentXmlSerializer.startTag(null, ERROR);
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw, true);
            testInfo.error.printStackTrace(pw);
            currentXmlSerializer.text(sw.toString());
            currentXmlSerializer.endTag(null, ERROR);
        }
        if (testInfo.failure != null) {
            currentXmlSerializer.startTag(null, FAILURE);
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw, true);
            testInfo.failure.printStackTrace(pw);
            currentXmlSerializer.text(sw.toString());
            currentXmlSerializer.endTag(null, FAILURE);
        }
        Processor eventProcessor = NewSolo.getEventProcessor();
        currentXmlSerializer.startTag(null,TEST_STEP);
        eventProcessor.process(currentXmlSerializer,testInfo.name);
        currentXmlSerializer.endTag(null, TEST_STEP);

        currentXmlSerializer.endTag(null, TESTCASE);
    }
}
