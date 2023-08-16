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


package org.kie.workbench.common.stunner.core.definition.adapter.bootstrap;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;

import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;

class BootstrapDefinitionSetAdapter implements DefinitionSetAdapter<Object> {

    private final AdapterRegistry adapterRegistry;

    BootstrapDefinitionSetAdapter(final AdapterRegistry adapterRegistry) {
        this.adapterRegistry = adapterRegistry;
    }

    @Override
    public String getId(final Object pojo) {
        return getWrapped(pojo).getId(pojo);
    }

    @Override
    public String getDomain(final Object pojo) {
        return getWrapped(pojo).getDomain(pojo);
    }

    @Override
    public String getDescription(final Object pojo) {
        return getWrapped(pojo).getDescription(pojo);
    }

    @Override
    public Set<String> getDefinitions(final Object pojo) {
        return getWrapped(pojo).getDefinitions(pojo);
    }

    @Override
    public Class<? extends ElementFactory> getGraphFactoryType(final Object pojo) {
        return getWrapped(pojo).getGraphFactoryType(pojo);
    }

    @Override
    public Annotation getQualifier(final Object pojo) {
        return getWrapped(pojo).getQualifier(pojo);
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean accepts(final Class<?> type) {
        return null != getWrapped(type);
    }

    private DefinitionSetAdapter<Object> getWrapped(final Object pojo) {
        return getWrapped(pojo.getClass());
    }

    private DefinitionSetAdapter<Object> getWrapped(final Class<?> type) {
        return adapterRegistry.getDefinitionSetAdapter(type);
    }

    @Override
    public Optional<String> getSvgNodeId(Object pojo) {
        return getWrapped(pojo).getSvgNodeId(pojo);
    }
}
