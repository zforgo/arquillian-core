package org.jboss.arquillian.junit5;

import java.lang.reflect.Method;
import java.util.ServiceLoader;
import org.jboss.arquillian.test.spi.LifecycleMethodExecutor;
import org.jboss.arquillian.test.spi.NoMethodExecutor;
import org.jboss.arquillian.test.spi.TestMethodExecutor;
import org.jboss.arquillian.test.spi.TestResult;
import org.jboss.arquillian.test.spi.TestRunnerAdaptor;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

public class ArquillianExtension implements BeforeAllCallback, BeforeEachCallback,
    BeforeTestExecutionCallback, TestExecutionExceptionHandler, AfterTestExecutionCallback, AfterEachCallback,
    AfterAllCallback, ExecutionCondition {

    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        new JUnit5TestClassLifecycleManager(extensionContext).beforeTestClassPhase(
            extensionContext.getRequiredTestClass());
    }

    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        beforeEachTestMethod(new Storage(extensionContext).getAdaptor(), extensionContext.getRequiredTestInstance(),
            extensionContext.getRequiredTestMethod());
    }

    void beforeEachTestMethod(TestRunnerAdaptor adaptor, Object testInstance, Method testMethod) throws Exception {
        adaptor.before(testInstance, testMethod, LifecycleMethodExecutor.NO_OP);
    }

    public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
        beforeTestMethodExecution(new Storage(extensionContext),
            new NoMethodExecutor(extensionContext.getRequiredTestMethod(), extensionContext.getRequiredTestInstance()));
    }

    void beforeTestMethodExecution(Storage storage, TestMethodExecutor testMethodExecutor) throws Exception {
        storage.storeTestResult(testMethodExecutor.getMethod(), TestResult.notRun());
        storage.getAdaptor().test(testMethodExecutor);
    }

    public void handleTestExecutionException(ExtensionContext extensionContext, Throwable throwable) throws Throwable {
        TestResult result = TestResult.failed(throwable);
        result.setEnd(System.currentTimeMillis());
        new Storage(extensionContext)
            .storeTestResult(extensionContext.getRequiredTestMethod(), result);
        throw throwable;
    }

    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        Storage storage = new Storage(extensionContext);
        TestResult result = storage.getTestResult(extensionContext.getRequiredTestMethod());
        if (result.getStatus() == TestResult.Status.NOT_RUN) {
            result = TestResult.passed();
            result.setEnd(System.currentTimeMillis());
            storage.storeTestResult(extensionContext.getRequiredTestMethod(), result);
        }
        SetTestResult setTestResultEvent =
            new SetTestResult(extensionContext.getRequiredTestInstance(), extensionContext.getRequiredTestMethod(),
                result);
        storage.getAdaptor().fire(setTestResultEvent);
    }

    public void afterEach(ExtensionContext extensionContext) throws Exception {
        new Storage(extensionContext).getAdaptor()
            .after(
                extensionContext.getRequiredTestInstance(),
                extensionContext.getRequiredTestMethod(),
                LifecycleMethodExecutor.NO_OP);
    }

    void afterEachTestMethod(TestRunnerAdaptor adaptor, Object testInstance, Method testMethod) throws Exception {
        adaptor.after(testInstance, testMethod, LifecycleMethodExecutor.NO_OP);
    }

    public void afterAll(ExtensionContext extensionContext) throws Exception {
        for (BeforeAfterTestClassPhaseCallback callback : ServiceLoader.load(BeforeAfterTestClassPhaseCallback.class)) {
            callback.process(extensionContext);
        }
        new JUnit5TestClassLifecycleManager(extensionContext).afterTestClassPhase(
            extensionContext.getRequiredTestClass());
    }

    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        ConditionEvaluationResult result = ConditionEvaluationResult.enabled("Nothing evaluated");

        for (ExecutionConditionEvaluator evaluator : ServiceLoader.load(ExecutionConditionEvaluator.class)) {
            result = evaluator.evaluateExecutionCondition(context, result);
        }
        return result;
    }
}
