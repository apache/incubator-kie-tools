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

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.ConnectionRuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;

/**
 * Manager for connection rules specific for Stunner's graph domain.
 */

public interface GraphConnectionRuleManager
        extends ConnectionRuleManager {

    /**
     * It checks connection rules and evaluates if the given connector candidate can be
     * attached to the given/source node.
     * @param edge The edge instance.
     * @param incomingNode The incoming node.
     * @param outgoingNode The outgoing node
     */
    RuleViolations evaluate( final Edge<? extends View<?>, ? extends Node> edge,
                             final Node<? extends View<?>, ? extends Edge> outgoingNode,
                             final Node<? extends View<?>, ? extends Edge> incomingNode );
}
