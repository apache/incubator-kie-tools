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

package org.kie.workbench.common.stunner.core.registry.impl;

import java.util.ArrayList;
import java.util.HashMap;

import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.factory.Factory;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;
import org.kie.workbench.common.stunner.core.registry.command.CommandRegistry;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionRegistry;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.kie.workbench.common.stunner.core.registry.diagram.DiagramRegistry;
import org.kie.workbench.common.stunner.core.registry.factory.FactoryRegistry;
import org.kie.workbench.common.stunner.core.registry.rule.RuleHandlerRegistry;

public abstract class AbstractRegistryFactory implements RegistryFactory {

    private AdapterManager adapterManager;

    protected AbstractRegistryFactory() {
    }

    public AbstractRegistryFactory(final AdapterManager adapterManager) {
        this.adapterManager = adapterManager;
    }

    @Override
    public AdapterRegistry newAdapterRegistry() {
        return new AdapterRegistryImpl();
    }

    @Override
    public <T> TypeDefinitionSetRegistry<T> newDefinitionSetRegistry() {
        return new DefinitionSetMapRegistry<T>(adapterManager);
    }

    @Override
    public <T> TypeDefinitionRegistry<T> newDefinitionRegistry() {
        return DefinitionMapRegistry.build(adapterManager);
    }

    @Override
    public <C extends Command> CommandRegistry<C> newCommandRegistry() {
        return new CommandRegistryImpl<C>();
    }

    @Override
    public <T extends Factory<?>> FactoryRegistry<T> newFactoryRegistry() {
        return new FactoryRegistryImpl<T>(adapterManager);
    }

    @Override
    public <T extends Diagram> DiagramRegistry<T> newDiagramRegistry() {
        return new DiagramListRegistry<T>();
    }

    @Override
    public RuleHandlerRegistry newRuleHandlerRegistry() {
        return new RuleHandlerRegistryImpl();
    }

    public <T> MapRegistry<T> newMapRegistry(final KeyProvider<T> keyProvider) {
        return new MapRegistry<T>(keyProvider,
                                  new HashMap<String, T>());
    }

    public <T> MapRegistry<T> newMapRegistry(final KeyProvider<T> keyProvider,
                                             final java.util.Map<String, T> map) {
        return new MapRegistry<T>(keyProvider,
                                  map);
    }

    public <T> ListRegistry<T> newListRegistry(final KeyProvider<T> keyProvider) {
        return new ListRegistry<T>(keyProvider,
                                   new ArrayList<T>());
    }

    public <T> ListRegistry<T> newListRegistry(final KeyProvider<T> keyProvider,
                                               final java.util.List<T> list) {
        return new ListRegistry<T>(keyProvider,
                                   list);
    }
}
