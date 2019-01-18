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
package org.jboss.arquillian.junit5.extension;

import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.EventContext;
import org.jboss.arquillian.junit.State;
import org.jboss.arquillian.test.spi.TestResult;
import org.jboss.arquillian.test.spi.TestResult.Status;
import org.jboss.arquillian.test.spi.annotation.TestScoped;
import org.jboss.arquillian.test.spi.event.suite.AfterTestLifecycleEvent;

class UpdateTestResultBeforeAfter {

    @Inject
    @TestScoped
    private InstanceProducer<TestResult> testResult;

    public void update(@Observes(precedence = 99) EventContext<AfterTestLifecycleEvent> context, TestResult result) {
        if (State.caughtExceptionAfterJunit() != null) {
            result.setStatus(Status.FAILED);
            result.setThrowable(State.caughtExceptionAfterJunit());
        } else {
            result.setStatus(Status.PASSED);
            result.setThrowable(null);
        }
        context.proceed();
    }
}
