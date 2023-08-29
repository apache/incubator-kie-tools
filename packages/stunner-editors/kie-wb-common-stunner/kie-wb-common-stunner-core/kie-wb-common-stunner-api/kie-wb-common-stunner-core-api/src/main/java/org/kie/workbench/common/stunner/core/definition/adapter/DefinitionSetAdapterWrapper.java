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

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;

import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;

public abstract class DefinitionSetAdapterWrapper<T, A extends DefinitionSetAdapter<T>> implements DefinitionSetAdapter<T> {

    protected final A adapter;

    protected DefinitionSetAdapterWrapper() {
        this(null);
    }

    public DefinitionSetAdapterWrapper(final A adapter) {
        this.adapter = adapter;
    }

    @Override
    public String getId(final T pojo) {
        return adapter.getId(pojo);
    }

    @Override
    public String getDomain(final T pojo) {
        return adapter.getDomain(pojo);
    }

    @Override
    public String getDescription(final T pojo) {
        return adapter.getDescription(pojo);
    }

    @Override
    public Set<String> getDefinitions(final T pojo) {
        return adapter.getDefinitions(pojo);
    }

    @Override
    public Class<? extends ElementFactory> getGraphFactoryType(final T pojo) {
        return adapter.getGraphFactoryType(pojo);
    }

    @Override
    public Annotation getQualifier(final T pojo) {
        return adapter.getQualifier(pojo);
    }

    @Override
    public int getPriority() {
        return adapter.getPriority();
    }

    @Override
    public boolean accepts(final Class<?> type) {
        return adapter.accepts(type);
    }

    @Override
    public Optional<String> getSvgNodeId(T pojo) {
        return adapter.getSvgNodeId(pojo);
    }
}
