package quant.robotiumlibrary.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Stores information about particular test case class - containing all
 * tests for that class.
 *
 */
public class TestCaseInfo {
    public Package thePackage;
    public String testCaseClass;
    public Map<String, TestInfo> testMap = new LinkedHashMap<>();

}