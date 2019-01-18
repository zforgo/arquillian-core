package org.jboss.arquillian.junit5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor;

public class ContainerStorage extends Storage{

    private static final String IN_CONTAINER_TESTS_KEY = "inContainerTestsKey";

    ContainerStorage(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    void storeInContainerTest(TestMethodTestDescriptor methodTestDescriptor) {
        List list = getStore().get(IN_CONTAINER_TESTS_KEY, List.class);
        if (list == null){
            list = new ArrayList();
        }
        list.add(methodTestDescriptor);
        getStore().put(IN_CONTAINER_TESTS_KEY ,list);
    }

    List<TestMethodTestDescriptor> getInContainerTests(){
        return getStore().getOrComputeIfAbsent(IN_CONTAINER_TESTS_KEY, key -> Collections.emptyList(), List.class);
    }
}
