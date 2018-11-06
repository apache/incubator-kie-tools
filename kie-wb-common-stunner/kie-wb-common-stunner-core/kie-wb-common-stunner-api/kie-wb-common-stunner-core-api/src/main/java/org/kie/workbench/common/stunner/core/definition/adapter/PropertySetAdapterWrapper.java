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

package org.kie.workbench.common.stunner.core.definition.adapter;

import java.util.Set;

public abstract class PropertySetAdapterWrapper<T, A extends PropertySetAdapter<T>> implements PropertySetAdapter<T> {

    protected final A adapter;

    protected PropertySetAdapterWrapper() {
        this(null);
    }

    public PropertySetAdapterWrapper(final A adapter) {
        this.adapter = adapter;
    }

    @Override
    public String getId(final T pojo) {
        return adapter.getId(pojo);
    }

    @Override
    public String getName(final T pojo) {
        return adapter.getName(pojo);
    }

    @Override
    public Set<?> getProperties(final T pojo) {
        return adapter.getProperties(pojo);
    }

    @Override
    public <P> P getProperty(T pojo, String propertyName) {
        return adapter.getProperty(pojo, propertyName);
    }

    @Override
    public boolean isPojoModel() {
        return false;
    }

    @Override
    public int getPriority() {
        return adapter.getPriority();
    }

    @Override
    public boolean accepts(final Class<?> type) {
        return adapter.accepts(type);
    }
}
