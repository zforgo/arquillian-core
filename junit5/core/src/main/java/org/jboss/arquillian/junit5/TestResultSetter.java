package org.jboss.arquillian.junit5;

import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.TestResult;
import org.jboss.arquillian.test.spi.annotation.TestScoped;

public class TestResultSetter {

    @Inject
    @TestScoped
    private InstanceProducer<TestResult> testResult;

    public void setTestResult(@Observes SetTestResult event){
        testResult.set(event.getTestResult());
    }
}
