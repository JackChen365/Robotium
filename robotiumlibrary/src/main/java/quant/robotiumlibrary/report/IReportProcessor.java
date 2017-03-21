package quant.robotiumlibrary.report;

import java.io.File;
import java.io.IOException;

/**
 * Created by cz on 2017/3/20.
 */

public interface IReportProcessor {
    void start(File file) throws IOException;

    void process(ReportTestRunner.TestCaseInfo testCaseInfo) throws IOException;

    void end() throws IOException;
}
