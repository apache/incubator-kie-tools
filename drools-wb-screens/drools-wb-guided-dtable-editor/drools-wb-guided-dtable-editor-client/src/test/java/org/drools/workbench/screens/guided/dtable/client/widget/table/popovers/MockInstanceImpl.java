/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.table.popovers;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.enterprise.util.TypeLiteral;

public class MockInstanceImpl<T> implements Instance<T> {

    private final List<T> instances;

    public MockInstanceImpl( final List<T> instances ) {
        this.instances = instances;
    }

    @Override
    public Instance<T> select( final Annotation... annotations ) {
        return null;
    }

    @Override
    public <U extends T> Instance<U> select( final Class<U> aClass,
                                             final Annotation... annotations ) {
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
    public void destroy( final T columnDefinitionBuilder ) {

    }

    @Override
    public T get() {
        return null;
    }

    @Override
    public Iterator<T> iterator() {
        return instances.iterator();
    }

    @Override
    public <U extends T> Instance<U> select(TypeLiteral<U> subtype, Annotation... qualifiers) {
        // TODO Auto-generated method stub
        return null;
    }

}
