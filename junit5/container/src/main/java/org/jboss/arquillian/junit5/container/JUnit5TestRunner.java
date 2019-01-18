/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009 Red Hat Inc. and/or its affiliates and other contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.junit5.container;

import java.util.Collections;
import java.util.List;
import org.jboss.arquillian.container.test.spi.TestRunner;
import org.jboss.arquillian.junit.State;
import org.jboss.arquillian.test.spi.TestResult;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

/**
 * JUnit5TestRunner
 * <p>
 * A Implementation of the Arquillian TestRunner SPI for JUnit.
 */
public class JUnit5TestRunner implements TestRunner {
    /**
     * Overwrite to provide additional run listeners.
     */
    protected List<TestExecutionListener> getRunListeners() {
        return Collections.emptyList();
    }

    public TestResult execute(Class<?> testClass, String methodName) {
        TestResult testResult;
        ArquillianTestExecutionListener listener = new ArquillianTestExecutionListener();
        try {
            Launcher launcher = LauncherFactory.create();
            launcher.registerTestExecutionListeners(listener);

            for (TestExecutionListener additionalListener : getRunListeners()) {
                launcher.registerTestExecutionListeners(additionalListener);
            }

            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(DiscoverySelectors.selectMethod(testClass.getCanonicalName(), methodName))
                .build();

            launcher.execute(request);
            testResult = listener.getTestResult();
        } catch (Throwable th) {
            th.printStackTrace();
            testResult = TestResult.failed(th);
        }
        testResult.setEnd(System.currentTimeMillis());
        return testResult;
    }

    private class ArquillianTestExecutionListener implements TestExecutionListener {

        private TestResult testResult = TestResult.notRun();

        public void executionSkipped(TestIdentifier testIdentifier, String reason) {
            testResult = TestResult.skipped(reason);
        }

        public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
            TestExecutionResult.Status status = testExecutionResult.getStatus();

            if (testResult.getStatus() == TestResult.Status.FAILED){
                return;
            }

            if (status == TestExecutionResult.Status.FAILED) {
                testResult = TestResult.failed(testExecutionResult.getThrowable().orElseGet(null));

            } else {
                State.caughtTestException(null);
                testResult = TestResult.passed();
            }
        }

        private TestResult getTestResult() {
            return testResult;
        }
    }
}
