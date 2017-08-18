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
package org.kie.workbench.common.stunner.core.graph.command;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.rule.RuleManager;

public abstract class AbstractGraphCommandExecutionContext implements GraphCommandExecutionContext {

    private final transient DefinitionManager definitionManager;
    private final transient FactoryManager factoryManager;
    private final transient RuleManager ruleManager;
    private final transient Index<?, ?> graphIndex;

    public AbstractGraphCommandExecutionContext(final DefinitionManager definitionManager,
                                                final FactoryManager factoryManager,
                                                final RuleManager ruleManager,
                                                final Index<?, ?> graphIndex) {
        this.definitionManager = definitionManager;
        this.factoryManager = factoryManager;
        this.ruleManager = ruleManager;
        this.graphIndex = graphIndex;
    }

    @Override
    public DefinitionManager getDefinitionManager() {
        return definitionManager;
    }

    @Override
    public FactoryManager getFactoryManager() {
        return factoryManager;
    }

    @Override
    public Index<?, ?> getGraphIndex() {
        return graphIndex;
    }

    @Override
    public RuleManager getRuleManager() {
        return ruleManager;
    }
}