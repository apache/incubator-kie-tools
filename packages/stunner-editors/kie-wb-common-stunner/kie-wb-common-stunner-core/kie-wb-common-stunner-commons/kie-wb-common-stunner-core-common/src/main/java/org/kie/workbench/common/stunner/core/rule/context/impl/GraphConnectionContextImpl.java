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
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.context.GraphConnectionContext;

class GraphConnectionContextImpl
        extends AbstractGraphEvaluationContext<GraphConnectionContextImpl>
        implements GraphConnectionContext {

    private final Edge<? extends View<?>, ? extends Node> connector;
    private final Optional<Node<? extends View<?>, ? extends Edge>> source;
    private final Optional<Node<? extends View<?>, ? extends Edge>> target;

    GraphConnectionContextImpl(final Edge<? extends View<?>, ? extends Node> connector,
                               final Optional<Node<? extends View<?>, ? extends Edge>> source,
                               final Optional<Node<? extends View<?>, ? extends Edge>> target) {
        this.connector = connector;
        this.source = source;
        this.target = target;
    }

    @Override
    public String getName() {
        return "Connection";
    }

    @Override
    public Edge<? extends View<?>, ? extends Node> getConnector() {
        return connector;
    }

    @Override
    public Optional<Node<? extends View<?>, ? extends Edge>> getSource() {
        return source;
    }

    @Override
    public Optional<Node<? extends View<?>, ? extends Edge>> getTarget() {
        return target;
    }

    @Override
    public boolean isDefaultDeny() {
        return true;
    }

    @Override
    public Class<? extends RuleEvaluationContext> getType() {
        return GraphConnectionContext.class;
    }
}
