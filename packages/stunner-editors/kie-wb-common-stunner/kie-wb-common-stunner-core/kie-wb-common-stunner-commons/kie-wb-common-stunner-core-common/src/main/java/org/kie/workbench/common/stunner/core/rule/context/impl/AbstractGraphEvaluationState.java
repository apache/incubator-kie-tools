/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.rule.context.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.context.GraphEvaluationState;

public abstract class AbstractGraphEvaluationState implements GraphEvaluationState {

    private final Graph<?, ? extends Node> graph;

    protected AbstractGraphEvaluationState(final Graph<?, ? extends Node> graph) {
        this.graph = graph;
    }

    @Override
    public Graph<?, ? extends Node> getGraph() {
        return graph;
    }

    @SuppressWarnings("unchecked")
    static final Function<Node, Element> dockParentSupplier = node -> {
        final Optional<Node> dockParent = GraphUtils.getDockParent(node);
        return dockParent.orElse(null);
    };

    @SuppressWarnings("unchecked")
    static Collection<Edge<? extends View<?>, Node>> unmodifiableCast(Collection<Edge> edges) {
        Collection e = Collections.unmodifiableCollection(edges);
        return e;
    }
}
