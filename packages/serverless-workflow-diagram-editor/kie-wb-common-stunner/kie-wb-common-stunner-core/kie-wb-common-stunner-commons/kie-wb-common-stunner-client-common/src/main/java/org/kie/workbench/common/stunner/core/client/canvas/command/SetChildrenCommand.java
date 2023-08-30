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

package org.kie.workbench.common.stunner.core.client.canvas.command;

import java.util.Collection;
import java.util.Collections;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public class SetChildrenCommand extends AbstractCanvasGraphCommand {

    protected final Node parent;
    protected final Collection<Node<?, Edge>> candidates;

    public SetChildrenCommand(final Node parent,
                              final Collection<Node<?, Edge>> candidates) {
        this.parent = parent;
        this.candidates = candidates;
    }

    public SetChildrenCommand(final Node parent,
                              final Node<?, Edge> candidate) {
        this(parent, Collections.singleton(candidate));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
        return new org.kie.workbench.common.stunner.core.graph.command.impl.SetChildrenCommand(parent,
                                                                                               candidates);
    }

    @Override
    protected AbstractCanvasCommand newCanvasCommand(final AbstractCanvasHandler context) {
        return new SetCanvasChildrenCommand(parent,
                                            candidates);
    }

    public Node getParent() {
        return parent;
    }

    public Collection<Node<?, Edge>> getCandidates() {
        return candidates;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [parent=" + toUUID(parent) + "," +
                "candidates=" + toUUIDs(candidates) + "]";
    }
}
