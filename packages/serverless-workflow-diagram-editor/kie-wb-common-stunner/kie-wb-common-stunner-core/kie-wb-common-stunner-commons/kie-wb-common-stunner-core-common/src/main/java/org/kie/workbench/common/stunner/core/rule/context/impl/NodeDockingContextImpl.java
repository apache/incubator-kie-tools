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

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.context.NodeDockingContext;

class NodeDockingContextImpl
        extends AbstractGraphEvaluationContext<NodeDockingContextImpl>
        implements NodeDockingContext {

    private final Element<? extends Definition<?>> parent;
    private final Node<? extends Definition<?>, ? extends Edge> candidate;

    NodeDockingContextImpl(final Element<? extends Definition<?>> parent,
                           final Node<? extends Definition<?>, ? extends Edge> candidate) {
        this.parent = parent;
        this.candidate = candidate;
    }

    @Override
    public String getName() {
        return "Docking";
    }

    @Override
    public Element<? extends Definition<?>> getParent() {
        return parent;
    }

    @Override
    public Node<? extends Definition<?>, ? extends Edge> getCandidate() {
        return candidate;
    }

    @Override
    public boolean isDefaultDeny() {
        return true;
    }

    @Override
    public Class<? extends RuleEvaluationContext> getType() {
        return NodeDockingContext.class;
    }
}
