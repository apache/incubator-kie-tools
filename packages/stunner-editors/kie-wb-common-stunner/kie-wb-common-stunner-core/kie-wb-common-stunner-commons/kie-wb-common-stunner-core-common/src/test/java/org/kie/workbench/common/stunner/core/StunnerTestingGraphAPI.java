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


package org.kie.workbench.common.stunner.core;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetRuleAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.factory.impl.EdgeFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.GraphFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.NodeFactoryImpl;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public abstract class StunnerTestingGraphAPI {

    public StunnerTestingAPI api;
    public GraphFactoryImpl graphFactory;
    public NodeFactoryImpl nodeFactory;
    public EdgeFactoryImpl edgeFactory;
    public GraphCommandFactory commandFactory;
    public GraphCommandManager commandManager;

    public StunnerTestingGraphAPI() {
        init();
    }

    protected abstract void init();

    public DefinitionManager getDefinitionManager() {
        return api.definitionManager;
    }

    public DefinitionUtils getDefinitionUtils() {
        return api.definitionUtils;
    }

    public TypeDefinitionSetRegistry getDefinitionSetRegistry() {
        return api.definitionSetRegistry;
    }

    public AdapterManager getAdapterManager() {
        return api.adapterManager;
    }

    public AdapterRegistry getAdapterRegistry() {
        return api.adapterRegistry;
    }

    public DefinitionAdapter getDefinitionAdapter() {
        return api.definitionAdapter;
    }

    public PropertyAdapter getPropertyAdapter() {
        return api.propertyAdapter;
    }

    public DefinitionSetRuleAdapter getRuleAdapter() {
        return api.ruleAdapter;
    }

    public FactoryManager getFactoryManager() {
        return api.factoryManager;
    }

    public RuleManager getRuleManager() {
        return api.ruleManager;
    }
}
