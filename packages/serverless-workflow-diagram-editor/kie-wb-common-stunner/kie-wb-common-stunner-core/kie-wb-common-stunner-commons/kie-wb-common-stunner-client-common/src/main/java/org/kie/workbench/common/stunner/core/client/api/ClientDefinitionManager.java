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


package org.kie.workbench.common.stunner.core.client.api;

import java.util.Collection;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kie.j2cl.tools.di.annotation.CircularDependency;
import org.kie.j2cl.tools.di.core.BeanManager;
import org.kie.j2cl.tools.di.core.SyncBeanDef;
import org.kie.workbench.common.stunner.core.api.AbstractDefinitionManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetRuleAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.MorphAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.clone.CloneManager;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;

@ApplicationScoped
@CircularDependency
public class ClientDefinitionManager extends AbstractDefinitionManager {

    private final BeanManager beanManager;

    protected ClientDefinitionManager() {
        super();
        this.beanManager = null;
    }

    @Inject
    public ClientDefinitionManager(final BeanManager beanManager,
                                   final RegistryFactory registryFactory,
                                   final AdapterManager adapterManager,
                                   final CloneManager cloneManager) {
        super(registryFactory,
              adapterManager,
              cloneManager);
        this.beanManager = beanManager;
    }

    @PostConstruct
    @SuppressWarnings("all")
    public void init() {
        // DefinitionSet client adapters.
        Collection<SyncBeanDef<DefinitionSetAdapter>> beanDefSetAdapters = beanManager.lookupBeans(DefinitionSetAdapter.class);
        for (SyncBeanDef<DefinitionSetAdapter> defSet : beanDefSetAdapters) {
            DefinitionSetAdapter definitionSetAdapter = defSet.getInstance();
            addAdapter(definitionSetAdapter);
        }
        // DefinitionSetRule client adapters.
        Collection<SyncBeanDef<DefinitionSetRuleAdapter>> beanDefSetRuleAdapters = beanManager.lookupBeans(DefinitionSetRuleAdapter.class);
        for (SyncBeanDef<DefinitionSetRuleAdapter> defSet : beanDefSetRuleAdapters) {
            DefinitionSetRuleAdapter definitionSetRuleAdapter = defSet.getInstance();
            addAdapter(definitionSetRuleAdapter);
        }
        // Definition client adapters.
        Collection<SyncBeanDef<DefinitionAdapter>> beanDefAdapters = beanManager.lookupBeans(DefinitionAdapter.class);
        for (SyncBeanDef<DefinitionAdapter> defSet : beanDefAdapters) {
            DefinitionAdapter definitionAdapter = defSet.getInstance();
            addAdapter(definitionAdapter);
        }
        // Property client adapters.
        Collection<SyncBeanDef<PropertyAdapter>> beanPropAdapters = beanManager.lookupBeans(PropertyAdapter.class);
        for (SyncBeanDef<PropertyAdapter> defSet : beanPropAdapters) {
            PropertyAdapter propertyAdapter = defSet.getInstance();
            addAdapter(propertyAdapter);
        }
        // Morph adapters.
        Collection<SyncBeanDef<MorphAdapter>> beanMorphAdapters = beanManager.lookupBeans(MorphAdapter.class);
        for (SyncBeanDef<MorphAdapter> morphAdapter : beanMorphAdapters) {
            MorphAdapter morphAdapterInstance = morphAdapter.getInstance();
            addAdapter(morphAdapterInstance);
        }
    }
}
