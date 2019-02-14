/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.core.client.canvas.command;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

/**
 * Removes the parent-child relationship between two nodes.
 * Operation is done both model and canvas side.
 */
public class RemoveChildCommand extends AbstractCanvasGraphCommand {

    protected final Node parent;
    protected final Node child;

    public RemoveChildCommand(final Node parent,
                              final Node child) {
        this.parent = parent;
        this.child = child;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
        return new org.kie.workbench.common.stunner.core.graph.command.impl.RemoveChildCommand(parent,
                                                                                               child);
    }

    @Override
    protected AbstractCanvasCommand newCanvasCommand(final AbstractCanvasHandler context) {
        return new RemoveCanvasChildCommand(parent,
                                            child);
    }

    public Node getParent() {
        return parent;
    }

    public Node getCandidate() {
        return child;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [child=" + getUUID(getCandidate()) + "," +
                "parent=" + getUUID(getParent()) + "]";
    }
}
