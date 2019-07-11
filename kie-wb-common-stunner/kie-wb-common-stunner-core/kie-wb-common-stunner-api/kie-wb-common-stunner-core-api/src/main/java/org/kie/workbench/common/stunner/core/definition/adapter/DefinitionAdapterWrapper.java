/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.Optional;
import java.util.Set;

import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;

public abstract class DefinitionAdapterWrapper<T, A extends DefinitionAdapter<T>> implements DefinitionAdapter<T> {

    protected final A adapter;

    protected DefinitionAdapterWrapper() {
        this(null);
    }

    public DefinitionAdapterWrapper(final A adapter) {
        this.adapter = adapter;
    }

    @Override
    public DefinitionId getId(final T pojo) {
        return adapter.getId(pojo);
    }

    @Override
    public String getCategory(final T pojo) {
        return adapter.getCategory(pojo);
    }

    @Override
    public String getTitle(final T pojo) {
        return adapter.getTitle(pojo);
    }

    @Override
    public Optional<String> getNameField(T pojo) {
        return adapter.getNameField(pojo);
    }

    @Override
    public String getDescription(final T pojo) {
        return adapter.getDescription(pojo);
    }

    @Override
    public Set<String> getLabels(final T pojo) {
        return adapter.getLabels(pojo);
    }

    @Override
    public Set<?> getPropertySets(final T pojo) {
        return adapter.getPropertySets(pojo);
    }

    @Override
    public Set<?> getProperties(final T pojo) {
        return adapter.getProperties(pojo);
    }

    @Override
    public Optional<?> getProperty(T pojo, String propertyName) {
        return adapter.getProperty(pojo, propertyName);
    }

    @Override
    public Class<? extends ElementFactory> getGraphFactoryType(final T pojo) {
        return adapter.getGraphFactoryType(pojo);
    }

    @Override
    public Object getMetaProperty(final PropertyMetaTypes metaType,
                                  final T pojo) {
        return adapter.getMetaProperty(metaType,
                                       pojo);
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
