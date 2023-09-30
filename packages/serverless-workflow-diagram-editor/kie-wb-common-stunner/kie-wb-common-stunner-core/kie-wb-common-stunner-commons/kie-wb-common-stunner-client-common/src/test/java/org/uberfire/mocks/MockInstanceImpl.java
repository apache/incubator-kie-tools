/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.uberfire.mocks;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Iterator;

import jakarta.enterprise.inject.Instance;

public class MockInstanceImpl<T> implements Instance<T> {

    private final T[] instances;

    public MockInstanceImpl(T... instance) {
        this.instances = instance;
    }

    @Override
    public Instance<T> select(Annotation... annotations) {
        return null;
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
    public void destroy(T instance) {

    }

    @Override
    public <U extends T> Instance<U> select(Class<U> aClass,
                                            Annotation... annotations) {
        return null;
    }

    @Override
    public Iterator<T> iterator() {
        return Arrays.asList(instances).iterator();
    }

    @Override
    public T get() {
        return instances[0];
    }

}
