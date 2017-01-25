/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.rule.graph.GraphRulesManager;

@NonPortable
public class GraphCommandExecutionContextImpl extends AbstractGraphCommandExecutionContext {

    private final transient GraphRulesManager rulesManager;

    public GraphCommandExecutionContextImpl(final DefinitionManager definitionManager,
                                            final FactoryManager factoryManager,
                                            final GraphRulesManager rulesManager,
                                            final Index<?, ?> graphIndex) {
        super(definitionManager,
              factoryManager,
              graphIndex);
        this.rulesManager = rulesManager;
    }

    @Override
    public GraphRulesManager getRulesManager() {
        return rulesManager;
    }
}
