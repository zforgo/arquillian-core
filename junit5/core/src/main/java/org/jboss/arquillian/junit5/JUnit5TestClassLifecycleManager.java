package org.jboss.arquillian.junit5;

import org.jboss.arquillian.junit.ArquillianTestClassLifecycleManager;
import org.jboss.arquillian.test.spi.TestRunnerAdaptor;
import org.junit.jupiter.api.extension.ExtensionContext;

public class JUnit5TestClassLifecycleManager extends ArquillianTestClassLifecycleManager {

    private Storage storage;

    public JUnit5TestClassLifecycleManager(ExtensionContext extensionContext) {
        storage = new Storage(extensionContext);
    }

    protected void setAdaptor(TestRunnerAdaptor testRunnerAdaptor) {
        storage.storeAdaptor(testRunnerAdaptor);
    }

    protected TestRunnerAdaptor getAdaptor() {
        return storage.getAdaptor();
    }
}
