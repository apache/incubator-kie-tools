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

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kie.j2cl.tools.di.annotation.CircularDependency;
import org.kie.workbench.common.stunner.core.definition.adapter.bootstrap.BootstrapAdapterFactory;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.registry.impl.AdapterRegistryImpl;

@ApplicationScoped
@CircularDependency
public class AdapterManagerImpl implements AdapterManager {

    private AdapterRegistry registry;
    private DefinitionSetAdapter<Object> definitionSetAdapter;
    private DefinitionSetRuleAdapter<Object> definitionSetRuleAdapter;
    private DefinitionAdapter<Object> definitionAdapter;
    private PropertyAdapter<Object, Object> propertyAdapter;

    private BootstrapAdapterFactory bootstrapAdapterFactory;

    protected AdapterManagerImpl() {
        this.registry = null;
        this.definitionSetAdapter = null;
        this.definitionSetRuleAdapter = null;
        this.definitionAdapter = null;
        this.propertyAdapter = null;
    }

    @Inject
    public AdapterManagerImpl(final BootstrapAdapterFactory bootstrapAdapterFactory) {
        this.registry = new AdapterRegistryImpl();
        this.bootstrapAdapterFactory = bootstrapAdapterFactory;
    }

    @PostConstruct
    public void init() {
        this.definitionSetAdapter = bootstrapAdapterFactory.newDefinitionSetAdapter(registry);
        this.definitionSetRuleAdapter = bootstrapAdapterFactory.newDefinitionSetRuleAdapter(registry);
        this.definitionAdapter = bootstrapAdapterFactory.newDefinitionAdapter(registry);
        this.propertyAdapter = bootstrapAdapterFactory.newPropertyAdapter(registry);
    }

    @Override
    public DefinitionSetAdapter<Object> forDefinitionSet() {
        return definitionSetAdapter;
    }

    @Override
    public DefinitionSetRuleAdapter<Object> forRules() {
        return definitionSetRuleAdapter;
    }

    @Override
    public DefinitionAdapter<Object> forDefinition() {
        return definitionAdapter;
    }

    @Override
    public PropertyAdapter<Object, Object> forProperty() {
        return propertyAdapter;
    }

    @Override
    public AdapterRegistry registry() {
        return registry;
    }
}
