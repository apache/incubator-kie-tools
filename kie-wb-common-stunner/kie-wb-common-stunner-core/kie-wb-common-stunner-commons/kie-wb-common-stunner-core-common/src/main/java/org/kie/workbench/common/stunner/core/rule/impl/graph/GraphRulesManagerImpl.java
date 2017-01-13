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

import org.kie.workbench.common.stunner.core.rule.graph.GraphCardinalityRuleManager;
import org.kie.workbench.common.stunner.core.rule.graph.GraphConnectionRuleManager;
import org.kie.workbench.common.stunner.core.rule.graph.GraphContainmentRuleManager;
import org.kie.workbench.common.stunner.core.rule.graph.GraphDockingRuleManager;
import org.kie.workbench.common.stunner.core.rule.graph.GraphEdgeCardinalityRuleManager;
import org.kie.workbench.common.stunner.core.rule.graph.GraphRulesManager;
import org.kie.workbench.common.stunner.core.rule.impl.AbstractRulesManager;

@Dependent
public class GraphRulesManagerImpl extends AbstractRulesManager<GraphContainmentRuleManager, GraphConnectionRuleManager,
        GraphCardinalityRuleManager, GraphEdgeCardinalityRuleManager, GraphDockingRuleManager> implements GraphRulesManager {

    private static final String NAME = "Graph Rules Manager";

    @Inject
    public GraphRulesManagerImpl(final GraphContainmentRuleManager containmentRuleManager,
                                 final GraphConnectionRuleManager connectionRuleManager,
                                 final GraphCardinalityRuleManager cardinalityRuleManager,
                                 final GraphEdgeCardinalityRuleManager edgeCardinalityRuleManager,
                                 final GraphDockingRuleManager graphDockingRuleManager) {
        super(containmentRuleManager,
              connectionRuleManager,
              cardinalityRuleManager,
              edgeCardinalityRuleManager,
              graphDockingRuleManager);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
