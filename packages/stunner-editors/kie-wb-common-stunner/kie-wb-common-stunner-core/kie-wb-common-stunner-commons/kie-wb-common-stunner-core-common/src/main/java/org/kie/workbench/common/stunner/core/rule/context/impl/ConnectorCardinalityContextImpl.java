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

import java.util.Optional;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.ConnectorCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.EdgeCardinalityContext;

public class ConnectorCardinalityContextImpl
        extends AbstractGraphEvaluationContext<ConnectorCardinalityContextImpl>
        implements ConnectorCardinalityContext {

    private final Element<? extends View<?>> candidate;
    private final Edge<? extends View<?>, Node> edge;
    private final EdgeCardinalityContext.Direction direction;
    private final Optional<CardinalityContext.Operation> operation;

    protected ConnectorCardinalityContextImpl(final Element<? extends View<?>> candidate,
                                              final Edge<? extends View<?>, Node> edge,
                                              final EdgeCardinalityContext.Direction direction,
                                              final Optional<CardinalityContext.Operation> operation) {
        this.candidate = candidate;
        this.edge = edge;
        this.direction = direction;
        this.operation = operation;
    }

    @Override
    public String getName() {
        return "Connector cardinality";
    }

    @Override
    public Element<? extends View<?>> getCandidate() {
        return candidate;
    }

    @Override
    public Optional<CardinalityContext.Operation> getOperation() {
        return operation;
    }

    @Override
    public boolean isDefaultDeny() {
        return false;
    }

    @Override
    public Class<? extends RuleEvaluationContext> getType() {
        return ConnectorCardinalityContext.class;
    }

    @Override
    public Edge<? extends View<?>, Node> getEdge() {
        return edge;
    }

    @Override
    public EdgeCardinalityContext.Direction getDirection() {
        return direction;
    }
}
