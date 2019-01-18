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

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.container.test.spi.TestRunner;
import org.jboss.arquillian.container.test.spi.client.deployment.CachedAuxilliaryArchiveAppender;
import org.jboss.arquillian.junit.ArquillianTestClassLifecycleManager;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.engine.JupiterTestEngine;
import org.junit.platform.engine.TestEngine;

/**
 * JUnit5DeploymentAppender
 * <p>
 * Package up the JUnit / Arquillian JUnit 5 related dependencies.
 */
public class JUnit5DeploymentAppender extends CachedAuxilliaryArchiveAppender {
    @Override
    protected Archive<?> buildArchive() {
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "arquillian-junit5.jar")
            .addPackages(
                true,
                "org.junit",
                "org/opentest4j",
                ArquillianExtension.class.getPackage().getName(),
                ArquillianTestClassLifecycleManager.class.getPackage().getName())
            .addAsServiceProvider(
                TestRunner.class,
                JUnit5TestRunner.class)
            .addAsServiceProvider(TestEngine.class, JupiterTestEngine.class)
            .addClasses(JUnit5RemoteExtension.class)
            .addAsServiceProvider(
                RemoteLoadableExtension.class,
                JUnit5RemoteExtension.class);
        return archive;
    }
}
