/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.graph.command;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.graph.GraphRulesManager;

@NonPortable
public class GraphCommandExecutionContextImpl implements GraphCommandExecutionContext {

    protected final transient DefinitionManager definitionManager;
    protected final transient FactoryManager factoryManager;
    protected final transient GraphRulesManager rulesManager;
    protected final transient GraphUtils graphUtils;

    public GraphCommandExecutionContextImpl( final DefinitionManager definitionManager,
                                             final FactoryManager factoryManager,
                                             final GraphRulesManager rulesManager,
                                             final GraphUtils graphUtils ) {
        this.definitionManager = definitionManager;
        this.factoryManager = factoryManager;
        this.rulesManager = rulesManager;
        this.graphUtils = graphUtils;
    }

    @Override
    public DefinitionManager getDefinitionManager() {
        return definitionManager;
    }

    public GraphUtils getGraphUtils() {
        return graphUtils;
    }

    @Override
    public FactoryManager getFactoryManager() {
        return factoryManager;
    }

    @Override
    public GraphRulesManager getRulesManager() {
        return rulesManager;
    }

}
