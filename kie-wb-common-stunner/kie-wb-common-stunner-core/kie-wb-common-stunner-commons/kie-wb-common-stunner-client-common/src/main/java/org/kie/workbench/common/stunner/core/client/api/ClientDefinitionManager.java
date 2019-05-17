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

package org.kie.workbench.common.stunner.core.client.api;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.stunner.core.api.AbstractDefinitionManager;
import org.kie.workbench.common.stunner.core.definition.DefinitionSetProxy;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetRuleAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.MorphAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertySetAdapter;
import org.kie.workbench.common.stunner.core.definition.clone.CloneManager;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;

@ApplicationScoped
public class ClientDefinitionManager extends AbstractDefinitionManager {

    private final SyncBeanManager beanManager;

    protected ClientDefinitionManager() {
        super();
        this.beanManager = null;
    }

    @Inject
    public ClientDefinitionManager(final SyncBeanManager beanManager,
                                   final RegistryFactory registryFactory,
                                   final AdapterManager adapterManager,
                                   final CloneManager cloneManager
    ) {
        super(registryFactory,
              adapterManager,
              cloneManager);
        this.beanManager = beanManager;
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        // DefinitionSet client adapters.
        Collection<SyncBeanDef<DefinitionSetAdapter>> beanDefSetAdapters = beanManager.lookupBeans(DefinitionSetAdapter.class);
        for (SyncBeanDef<DefinitionSetAdapter> defSet : beanDefSetAdapters) {
            DefinitionSetAdapter definitionSet = defSet.getInstance();
            addAdapter(definitionSet);
        }
        // DefinitionSetRule client adapters.
        Collection<SyncBeanDef<DefinitionSetRuleAdapter>> beanDefSetRuleAdapters = beanManager.lookupBeans(DefinitionSetRuleAdapter.class);
        for (SyncBeanDef<DefinitionSetRuleAdapter> defSet : beanDefSetRuleAdapters) {
            DefinitionSetRuleAdapter definitionSet = defSet.getInstance();
            addAdapter(definitionSet);
        }
        // Definition client adapters.
        Collection<SyncBeanDef<DefinitionAdapter>> beanDefAdapters = beanManager.lookupBeans(DefinitionAdapter.class);
        for (SyncBeanDef<DefinitionAdapter> defSet : beanDefAdapters) {
            DefinitionAdapter definitionSet = defSet.getInstance();
            addAdapter(definitionSet);
        }
        // PropertySet client adapters.
        Collection<SyncBeanDef<PropertySetAdapter>> beanPropSetAdapters = beanManager.lookupBeans(PropertySetAdapter.class);
        for (SyncBeanDef<PropertySetAdapter> defSet : beanPropSetAdapters) {
            PropertySetAdapter definitionSet = defSet.getInstance();
            addAdapter(definitionSet);
        }
        // Property client adapters.
        Collection<SyncBeanDef<PropertyAdapter>> beanPropAdapters = beanManager.lookupBeans(PropertyAdapter.class);
        for (SyncBeanDef<PropertyAdapter> defSet : beanPropAdapters) {
            PropertyAdapter definitionSet = defSet.getInstance();
            addAdapter(definitionSet);
        }
        // Morph adapters.
        Collection<SyncBeanDef<MorphAdapter>> beanMorphAdapters = beanManager.lookupBeans(MorphAdapter.class);
        for (SyncBeanDef<MorphAdapter> morphAdapter : beanMorphAdapters) {
            MorphAdapter instance = morphAdapter.getInstance();
            addAdapter(instance);
        }
        // Once adapters present, add the Definition Sets found on current context.
        Collection<SyncBeanDef<DefinitionSetProxy>> beanDefSets = beanManager.lookupBeans(DefinitionSetProxy.class);
        for (SyncBeanDef<DefinitionSetProxy> defSet : beanDefSets) {
            DefinitionSetProxy definitionSetProxy = defSet.getInstance();
            Object definitionSet = definitionSetProxy.getDefinitionSet();
            addDefinitionSet(definitionSet);
        }
    }
}
