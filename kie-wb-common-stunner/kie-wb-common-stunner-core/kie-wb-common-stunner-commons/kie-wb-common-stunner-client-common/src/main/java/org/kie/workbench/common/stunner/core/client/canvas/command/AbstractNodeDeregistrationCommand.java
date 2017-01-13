/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.canvas.command;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CompositeCommand;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommandImpl;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.SafeDeleteNodeCommand;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.SafeDeleteNodeProcessor;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public abstract class AbstractNodeDeregistrationCommand extends AbstractCanvasGraphCommand {

    private final Node candidate;
    private transient CompositeCommand<AbstractCanvasHandler, CanvasViolation> command;

    @SuppressWarnings("unchecked")
    public AbstractNodeDeregistrationCommand(final Node candidate) {
        this.candidate = candidate;
        this.command = new CompositeCommandImpl.CompositeCommandBuilder<AbstractCanvasHandler, CanvasViolation>()
                .reverse()
                .build();
    }

    protected abstract Command<AbstractCanvasHandler, CanvasViolation> getDeregistrationCommand();

    @Override
    @SuppressWarnings("unchecked")
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
        return new SafeDeleteNodeCommand(candidate,
                                         getSafeDeleteCallback(context));
    }

    protected SafeDeleteNodeProcessor.Callback getSafeDeleteCallback(final AbstractCanvasHandler context) {
        return safeDeleteCallback;
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler context) {
        return command;
    }

    public Node getCandidate() {
        return candidate;
    }

    protected CompositeCommand<AbstractCanvasHandler, CanvasViolation> getCommand() {
        return command;
    }

    private final SafeDeleteNodeProcessor.Callback safeDeleteCallback = new SafeDeleteNodeProcessor.Callback() {
        @Override
        public void deleteChildNode(final Node<Definition<?>, Edge> node) {
            // No recursion here.
        }

        @Override
        public void deleteInViewEdge(final Edge<View<?>, Node> edge) {
            getCommand().addCommand(new SetCanvasConnectionCommand(edge));
        }

        @Override
        public void deleteInChildEdge(final Edge<Child, Node> edge) {
            final Node parent = edge.getSourceNode();
            final Node candidate = edge.getTargetNode();
            getCommand().addCommand(new RemoveCanvasChildCommand(parent,
                                                                 candidate));
        }

        @Override
        public void deleteInDockEdge(final Edge<Dock, Node> edge) {
            final Node parent = edge.getSourceNode();
            final Node candidate = edge.getTargetNode();
            getCommand().addCommand(new CanvasUndockNodeCommand(parent,
                                                                candidate));
        }

        @Override
        public void deleteOutViewEdge(final Edge<? extends View<?>, Node> edge) {
            getCommand().addCommand(new SetCanvasConnectionCommand(edge));
        }

        @Override
        public void deleteNode(final Node<Definition<?>, Edge> node) {
            if (node.equals(getCandidate())) {
                getCommand().addCommand(getDeregistrationCommand());
            } else {
                getCommand().addCommand(new DeleteCanvasNodeCommand(node));
            }
        }
    };
}
