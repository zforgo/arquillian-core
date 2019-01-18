package org.jboss.arquillian.junit5;

import java.lang.reflect.Field;
import org.jboss.arquillian.container.test.impl.client.deployment.event.TestModeInfoEvent;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor;

public class SkipIfRunningInContainerEvaluator implements ExecutionConditionEvaluator {
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context,
        ConditionEvaluationResult previousResult) {

        ContainerStorage storage = new ContainerStorage(context.getParent().get());
        if (context.getTestMethod().isPresent()) {
            TestModeInfoEvent testInfo =
                new TestModeInfoEvent(context.getRequiredTestClass(), context.getRequiredTestMethod());
            storage.getAdaptor().fire(testInfo);

            if (!testInfo.isRunAsClient()) {
                try {
                    Field field =
                        SecurityActions.getField(context.getClass(), "testDescriptor");
                    TestMethodTestDescriptor methodTestDescriptor =
                        (TestMethodTestDescriptor) field.get(context);
                    storage.storeInContainerTest(methodTestDescriptor);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return ConditionEvaluationResult.disabled("Should run in-container");
            }
        }
        return ConditionEvaluationResult.enabled("Should run as client");
    }
}
