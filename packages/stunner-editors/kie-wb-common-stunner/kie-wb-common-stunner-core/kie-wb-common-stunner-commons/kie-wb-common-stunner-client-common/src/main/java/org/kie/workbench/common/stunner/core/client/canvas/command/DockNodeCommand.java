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

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public class DockNodeCommand extends AbstractCanvasGraphCommand {

    private final Node parent;
    private final Node candidate;
    private boolean adjustPosition;

    public DockNodeCommand(final Node parent,
                           final Node candidate) {
        this(parent, candidate, false);
    }

    public DockNodeCommand(final Node parent,
                           final Node candidate,
                           final boolean adjustPosition) {
        this.parent = parent;
        this.candidate = candidate;
        this.adjustPosition = adjustPosition;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
        return new org.kie.workbench.common.stunner.core.graph.command.impl.DockNodeCommand(parent,
                                                                                            candidate);
    }

    @Override
    protected AbstractCanvasCommand newCanvasCommand(final AbstractCanvasHandler context) {
        return (adjustPosition ?
                new CanvasDockNodeCommand(parent, candidate,
                                          position -> new UpdateElementPositionCommand(candidate, position).execute(context)) :
                new CanvasDockNodeCommand(parent, candidate));
    }

    public Node getParent() {
        return parent;
    }

    public Node getCandidate() {
        return candidate;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [candidate=" + toUUID(getCandidate()) + "," +
                "parent=" + toUUID(getParent()) + "]";
    }
}