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

package org.kie.workbench.common.stunner.core.rule.impl.graph;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.rule.DockingRule;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.graph.GraphDockingRuleManager;
import org.kie.workbench.common.stunner.core.rule.model.ModelDockingRuleManager;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class GraphDockingRuleManagerImpl extends AbstractGraphRuleManager<DockingRule, ModelDockingRuleManager>
        implements GraphDockingRuleManager {

    private static final String NAME = "Graph Docking Rule Manager";

    ModelDockingRuleManager modelDockingRuleManager;

    @Inject
    public GraphDockingRuleManagerImpl( final DefinitionManager definitionManager,
                                        final ModelDockingRuleManager modelDockingRuleManager ) {
        super( definitionManager );
        this.modelDockingRuleManager = modelDockingRuleManager;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected ModelDockingRuleManager getWrapped() {
        return modelDockingRuleManager;
    }

    @Override
    public RuleViolations evaluate( final Element<?> target,
                                    final Element<? extends Definition<?>> candidateRoles ) {
        final String targetId = getElementDefinitionId( target );
        return modelDockingRuleManager.evaluate( targetId, getLabels( candidateRoles ) );

    }

}
