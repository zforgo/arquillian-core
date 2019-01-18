package org.jboss.arquillian.test.spi;

import java.lang.reflect.Method;

public class NoMethodExecutor implements TestMethodExecutor {

    private final Method method;
    private final Object testInstance;

    public NoMethodExecutor(Method method, Object testInstance){
        this.method = method;
        this.testInstance = testInstance;
    }

    public Method getMethod() {
        return method;
    }

    public Object getInstance() {
        return testInstance;
    }

    public void invoke(Object... parameters) throws Throwable {
    }

    public abstract static class NoMethodExecutorWithTestResults extends NoMethodExecutor {

        public NoMethodExecutorWithTestResults(Method method, Object testInstance) {
            super(method, testInstance);
        }

        public abstract void setTestResult(TestResult testResult);
    }
}
