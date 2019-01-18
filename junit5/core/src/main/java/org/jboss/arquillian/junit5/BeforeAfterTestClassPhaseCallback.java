package org.jboss.arquillian.junit5;

import org.junit.jupiter.api.extension.ExtensionContext;

public interface BeforeAfterTestClassPhaseCallback {

    void process(ExtensionContext extensionContext) throws Exception;
}
