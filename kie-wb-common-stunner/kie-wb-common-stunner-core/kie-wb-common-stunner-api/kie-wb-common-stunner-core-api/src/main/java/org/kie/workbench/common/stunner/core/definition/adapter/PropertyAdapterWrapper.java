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

import java.util.Map;

import org.kie.workbench.common.stunner.core.definition.property.PropertyType;

public abstract class PropertyAdapterWrapper<T, V, A extends PropertyAdapter<T, V>> implements PropertyAdapter<T, V> {

    protected final A adapter;

    protected PropertyAdapterWrapper() {
        this(null);
    }

    public PropertyAdapterWrapper(final A adapter) {
        this.adapter = adapter;
    }

    @Override
    public String getId(final T pojo) {
        return adapter.getId(pojo);
    }

    @Override
    public PropertyType getType(final T pojo) {
        return adapter.getType(pojo);
    }

    @Override
    public String getCaption(final T pojo) {
        return adapter.getCaption(pojo);
    }

    @Override
    public String getDescription(final T pojo) {
        return adapter.getDescription(pojo);
    }

    @Override
    public boolean isReadOnly(final T pojo) {
        return adapter.isReadOnly(pojo);
    }

    @Override
    public boolean isOptional(final T pojo) {
        return adapter.isOptional(pojo);
    }

    @Override
    public V getValue(final T pojo) {
        return adapter.getValue(pojo);
    }

    @Override
    public Map<V, String> getAllowedValues(final T pojo) {
        return adapter.getAllowedValues(pojo);
    }

    @Override
    public void setValue(final T pojo,
                         final V value) {
        adapter.setValue(pojo,
                         value);
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
