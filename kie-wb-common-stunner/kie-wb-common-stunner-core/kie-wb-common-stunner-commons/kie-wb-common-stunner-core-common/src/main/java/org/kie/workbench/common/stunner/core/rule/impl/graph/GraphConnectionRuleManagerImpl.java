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

package org.kie.workbench.common.stunner.core.rule.impl.graph;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.ConnectionRule;
import org.kie.workbench.common.stunner.core.rule.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.graph.GraphConnectionRuleManager;
import org.kie.workbench.common.stunner.core.rule.model.ModelConnectionRuleManager;

@Dependent
public class GraphConnectionRuleManagerImpl extends AbstractGraphRuleManager<ConnectionRule, ModelConnectionRuleManager>
        implements GraphConnectionRuleManager {

    private static final String NAME = "Graph Connection Rule Manager";

    private ModelConnectionRuleManager modelConnectionRuleManager;

    @Inject
    public GraphConnectionRuleManagerImpl( final DefinitionManager definitionManager,
                                           final ModelConnectionRuleManager modelConnectionRuleManager ) {
        super( definitionManager );
        this.modelConnectionRuleManager = modelConnectionRuleManager;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected ModelConnectionRuleManager getWrapped() {
        return modelConnectionRuleManager;
    }

    @Override
    public RuleViolations evaluate( final Edge<? extends View<?>, ? extends Node> edge,
                                    final Node<? extends View<?>, ? extends Edge> outgoing,
                                    final Node<? extends View<?>, ? extends Edge> incoming ) {
        if ( incoming == null || outgoing == null ) {
            return new DefaultRuleViolations();
        }
        final String edgeId = getElementDefinitionId( edge );
        return modelConnectionRuleManager.evaluate( edgeId,
                                                    getLabels( outgoing ),
                                                    getLabels( incoming ) );
    }
}
