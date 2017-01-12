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

package org.kie.workbench.common.stunner.core.rule.graph;

import java.util.List;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.EdgeCardinalityRule;
import org.kie.workbench.common.stunner.core.rule.EdgeCardinalityRuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;

/**
 * Manager for connector's cardinality rules specific for Stunner's graph domain.
 */
public interface GraphEdgeCardinalityRuleManager
        extends EdgeCardinalityRuleManager {

    /**
     * It checks cardinality rules and evaluates if the given connector candidate added or removed into the structure.
     * @param edge The connector.
     * @param candidate The candidate node.
     * @param count The current connector's count for the node.
     * @param ruleType if it's an incoming or outgoing connection.
     * @param operation Can be adding a new connector, removing an existing one, or NONE, eg: just to validate rules
     * against current structure.
     */
    RuleViolations evaluate( final Edge<? extends View<?>, Node> edge,
                             final Node<? extends View<?>, Edge> candidate,
                             final List<? extends Edge> count,
                             final EdgeCardinalityRule.Type ruleType,
                             final Operation operation );
}
