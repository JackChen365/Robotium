package quant.robotiumlibrary.report;

import java.io.File;
import java.io.IOException;

import quant.robotiumlibrary.model.TestCaseInfo;

/**
 * Created by cz on 2017/3/20.
 */

public interface IReportProcessor {
    void start(File file) throws IOException;

    void process(TestCaseInfo testCaseInfo) throws IOException;

    void end() throws IOException;
}
