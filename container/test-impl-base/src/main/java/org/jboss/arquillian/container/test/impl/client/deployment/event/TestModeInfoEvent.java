package org.jboss.arquillian.container.test.impl.client.deployment.event;

import java.lang.reflect.Method;
import org.jboss.arquillian.test.spi.event.suite.ClassEvent;

public class TestModeInfoEvent extends ClassEvent {

    private final Method testMethod;
    private boolean runAsClient = true;

    public TestModeInfoEvent(Class<?> testClass, Method testMethod) {
        super(testClass);
        this.testMethod = testMethod;
    }

    public Method getTestMethod() {
        return testMethod;
    }

    public void setRunAsClient(boolean runAsClient) {
        this.runAsClient = runAsClient;
    }

    public boolean isRunAsClient() {
        return runAsClient;
    }
}
