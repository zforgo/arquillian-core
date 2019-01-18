package org.jboss.arquillian.junit5;

import java.lang.reflect.Method;
import org.jboss.arquillian.test.spi.TestResult;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;

public class SetTestResult extends TestEvent {
    private final TestResult testResult;

    public SetTestResult(Object testInstance, Method testMethod, TestResult testResult) {
        super(testInstance, testMethod);
        this.testResult = testResult;
    }

    public TestResult getTestResult() {
        return testResult;
    }
}
