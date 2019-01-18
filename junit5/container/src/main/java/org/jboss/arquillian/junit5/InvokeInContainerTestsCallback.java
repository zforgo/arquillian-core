package org.jboss.arquillian.junit5;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.jboss.arquillian.test.spi.NoMethodExecutor;
import org.jboss.arquillian.test.spi.TestResult;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.engine.descriptor.ClassExtensionContext;
import org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestExecutionResult;

public class InvokeInContainerTestsCallback implements BeforeAfterTestClassPhaseCallback {

    @Override
    public void process(ExtensionContext extensionContext) throws Exception {
        ContainerStorage storage = new ContainerStorage(extensionContext);
        if (storage.getInContainerTests().isEmpty()) {
            return;
        }
        ClassExtensionContext classExtensionContext = (ClassExtensionContext) extensionContext;
        Field field = SecurityActions.getField(classExtensionContext.getClass(), "engineExecutionListener");
        EngineExecutionListener listener = (EngineExecutionListener) field.get(classExtensionContext);
        ArquillianExtension arquillianExtension = new ArquillianExtension();

        storage.getInContainerTests()
            .forEach(descriptor -> {
                try {
                    Object test = descriptor.getTestClass().getConstructor().newInstance();
                    Method testMethod = descriptor.getTestMethod();
                    arquillianExtension.beforeEachTestMethod(storage.getAdaptor(), test, testMethod);

                    listener.executionStarted(descriptor);
                    arquillianExtension.beforeTestMethodExecution(storage,
                        new NoMethodExecutor.NoMethodExecutorWithTestResults(testMethod, test) {
                            @Override
                            public void setTestResult(TestResult testResult) {
                                setResult(testResult, listener, descriptor);
                            }
                        });

                    arquillianExtension.afterEachTestMethod(storage.getAdaptor(), test, testMethod);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
    }

    private void setResult(TestResult testResult, EngineExecutionListener listener,
        TestMethodTestDescriptor descriptor) {
        if (testResult.getStatus() == TestResult.Status.SKIPPED) {
            listener.executionSkipped(descriptor, testResult.getThrowable().getMessage());
        } else if (testResult.getStatus() == TestResult.Status.FAILED) {
            listener.executionFinished(descriptor, TestExecutionResult.failed(testResult.getThrowable()));
        } else {
            listener.executionFinished(descriptor, TestExecutionResult.successful());
        }
    }
}
