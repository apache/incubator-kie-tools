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

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.EdgeCardinalityRule;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.graph.GraphEdgeCardinalityRuleManager;
import org.kie.workbench.common.stunner.core.rule.model.ModelEdgeCardinalityRuleManager;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.List;

@Dependent
public class GraphEdgeCardinalityRuleManagerImpl extends AbstractGraphRuleManager<EdgeCardinalityRule, ModelEdgeCardinalityRuleManager>
        implements GraphEdgeCardinalityRuleManager {

    private static final String NAME = "Graph Edge Cardinality Rule Manager";

    private ModelEdgeCardinalityRuleManager modelEdgeCardinalityRuleManager;
    private GraphUtils graphUtils;

    @Inject
    public GraphEdgeCardinalityRuleManagerImpl( final DefinitionManager definitionManager,
                                                final GraphUtils graphUtils,
                                                final ModelEdgeCardinalityRuleManager modelEdgeCardinalityRuleManager ) {
        super( definitionManager );
        this.modelEdgeCardinalityRuleManager = modelEdgeCardinalityRuleManager;
        this.graphUtils = graphUtils;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected ModelEdgeCardinalityRuleManager getWrapped() {
        return modelEdgeCardinalityRuleManager;
    }

    @Override
    public RuleViolations evaluate( final Edge<? extends View<?>, Node> edge,
                                    final Node<? extends View<?>, Edge> node,
                                    final List<? extends Edge> edges,
                                    final EdgeCardinalityRule.Type ruleType,
                                    final Operation operation ) {
        // The edge defintiion's identifier.
        final String edgeId = getElementDefinitionId( edge );
        // Edge count.
        final int count = graphUtils.countEdges( edgeId, edges );
        // Delegate to the domain model cardinality rule manager.
        return modelEdgeCardinalityRuleManager.evaluate( edgeId, getLabels( node ), count, ruleType, operation );

    }

}
