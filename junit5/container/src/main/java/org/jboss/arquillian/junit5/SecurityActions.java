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
package org.jboss.arquillian.junit5;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * A set of privileged actions that are not to leak out
 * of this package
 *
 * @version $Revision: $
 */
final class SecurityActions {

    /**
     * No instantiation
     */
    private SecurityActions() {
        throw new UnsupportedOperationException("No instantiation");
    }

    static Field getField(final Class<?> source, final String name) {
        Field declaredAccessibleField = AccessController.doPrivileged(new PrivilegedAction<Field>() {
            public Field run() {
                Field foundField = null;
                Class<?> nextSource = source;
                while (nextSource != Object.class) {
                    try {
                        foundField = nextSource.getDeclaredField(name);
                        if (!foundField.isAccessible()) {
                            foundField.setAccessible(true);
                        }
                        break;
                    } catch (NoSuchFieldException e) {
                        // Nothing to do - just scan the super class
                    }
                    nextSource = nextSource.getSuperclass();
                }
                return foundField;
            }
        });
        return declaredAccessibleField;
    }
}
