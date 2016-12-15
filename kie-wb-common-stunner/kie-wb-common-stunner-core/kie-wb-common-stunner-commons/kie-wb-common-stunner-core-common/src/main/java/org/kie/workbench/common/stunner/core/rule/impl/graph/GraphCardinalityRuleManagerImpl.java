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
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.CardinalityRule;
import org.kie.workbench.common.stunner.core.rule.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.graph.GraphCardinalityRuleManager;
import org.kie.workbench.common.stunner.core.rule.model.ModelCardinalityRuleManager;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Map;
import java.util.Set;

@Dependent
public class GraphCardinalityRuleManagerImpl extends AbstractGraphRuleManager<CardinalityRule, ModelCardinalityRuleManager>
        implements GraphCardinalityRuleManager {

    private static final String NAME = "Graph Cardinality Rule Manager";

    private ModelCardinalityRuleManager modelCardinalityRuleManager;
    private GraphUtils graphUtils;

    @Inject
    public GraphCardinalityRuleManagerImpl( final DefinitionManager definitionManager,
                                            final GraphUtils graphUtils,
                                            final ModelCardinalityRuleManager modelCardinalityRuleManager ) {
        super( definitionManager );
        this.modelCardinalityRuleManager = modelCardinalityRuleManager;
        this.graphUtils = graphUtils;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected ModelCardinalityRuleManager getWrapped() {
        return modelCardinalityRuleManager;
    }

    @Override
    public RuleViolations evaluate( final Graph<?, ? extends Node> target,
                                    final Node<? extends View<?>, ? extends Edge> candidate,
                                    final Operation operation ) {
        final Set<String> labels = candidate.getLabels();
        final Map<String, Integer> graphLabelCount = GraphUtils.getLabelsCount( target, labels );
        final DefaultRuleViolations results = new DefaultRuleViolations();
        labels.stream().forEach( role -> {
            final Integer i = graphLabelCount.get( role );
            final RuleViolations violations =
                    modelCardinalityRuleManager.evaluate( role, null != i ? i : 0, operation );
            results.addViolations( violations );

        } );
        return results;
    }

}
