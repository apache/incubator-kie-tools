/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.registry.impl;

import java.util.HashMap;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.registry.impl.DefaultDefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionMapRegistry;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;

@ApplicationScoped
public class ClientDefinitionsCacheRegistry {

    private final DefaultDefinitionsCacheRegistry definitionsCacheRegistry;

    // CDI Proxy.
    public ClientDefinitionsCacheRegistry() {
        this.definitionsCacheRegistry = null;
    }

    @Inject
    public ClientDefinitionsCacheRegistry(final FactoryManager factoryManager,
                                          final AdapterManager adapterManager) {
        this.definitionsCacheRegistry =
                new DefaultDefinitionsCacheRegistry(factoryManager::newDefinition,
                                                    factoryManager::newDefinition,
                                                    DefinitionMapRegistry.build(adapterManager,
                                                                                new HashMap<>()));
    }

    @Produces
    public DefinitionsCacheRegistry getRegistry() {
        return definitionsCacheRegistry;
    }

    @PreDestroy
    public void destroy() {
        definitionsCacheRegistry.destroy();
    }
}
