/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationlisteditor;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import javax.enterprise.inject.Instance;

public class InstanceMock<T> implements Instance<T> {

    @Override
    public Instance< T > select( Annotation... annotations ) {
        return null;
    }

    @Override
    public < U extends T > Instance< U > select( Class< U > aClass, Annotation... annotations ) {
        return null;
    }

    @Override
    public boolean isUnsatisfied( ) {
        return false;
    }

    @Override
    public boolean isAmbiguous( ) {
        return false;
    }

    @Override
    public void destroy( T t ) {

    }

    @Override
    public Iterator< T > iterator( ) {
        return null;
    }

    @Override
    public T get( ) {
        return null;
    }
}
