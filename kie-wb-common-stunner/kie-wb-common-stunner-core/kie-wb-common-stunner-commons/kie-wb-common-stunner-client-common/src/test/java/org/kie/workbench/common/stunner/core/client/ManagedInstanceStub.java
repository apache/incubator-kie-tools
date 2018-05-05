/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Iterator;

import org.jboss.errai.ioc.client.api.ManagedInstance;

public class ManagedInstanceStub<T> implements ManagedInstance<T> {

    private final T[] instances;

    public ManagedInstanceStub(T... instances) {
        this.instances = instances;
    }

    @Override
    public ManagedInstance<T> select(Annotation... annotations) {
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends T> ManagedInstance<U> select(Class<U> aClass, Annotation... annotations) {
        return (ManagedInstance<U>) this;
    }

    @Override
    public boolean isUnsatisfied() {
        return false;
    }

    @Override
    public boolean isAmbiguous() {
        return false;
    }

    @Override
    public void destroy(T t) {
    }

    @Override
    public void destroyAll() {
    }

    @Override
    public Iterator<T> iterator() {
        return Arrays.asList(this.instances).iterator();
    }

    @Override
    public T get() {
        return instances[0];
    }
}
