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


package org.kie.workbench.common.stunner.core.definition.adapter;

import java.util.Optional;

import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;

public abstract class DefinitionAdapterWrapper<T, A extends DefinitionAdapter<T>>
        implements DefinitionAdapter<T> {

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
    public String getDescription(final T pojo) {
        return adapter.getDescription(pojo);
    }

    @Override
    public String[] getLabels(final T pojo) {
        return adapter.getLabels(pojo);
    }

    @Override
    public String[] getPropertyFields(final T pojo) {
        return adapter.getPropertyFields(pojo);
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
    public String getMetaPropertyField(final T pojo,
                                       final PropertyMetaTypes metaType) {
        return adapter.getMetaPropertyField(pojo, metaType);
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
