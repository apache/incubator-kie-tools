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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CompositeCommand;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommandImpl;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.SafeDeleteNodeCommand;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public class DeleteNodeCommand extends AbstractCanvasGraphCommand {

    private static Logger LOGGER = Logger.getLogger(DeleteNodeCommand.class.getName());

    private final Node candidate;
    private transient CompositeCommand<AbstractCanvasHandler, CanvasViolation> command;

    @SuppressWarnings("unchecked")
    public DeleteNodeCommand(final Node candidate) {
        this.candidate = candidate;
        this.command = new CompositeCommandImpl.CompositeCommandBuilder<AbstractCanvasHandler, CanvasViolation>()
                .reverse()
                .build();
    }

    public Node getCandidate() {
        return candidate;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
        return new SafeDeleteNodeCommand(candidate,
                                         new CanvasDeleteProcessor());
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler context) {
        return command;
    }

    protected CompositeCommand<AbstractCanvasHandler, CanvasViolation> getCommand() {
        return command;
    }

    private class CanvasDeleteProcessor implements SafeDeleteNodeCommand.SafeDeleteNodeCommandCallback {

        @Override
        public void deleteIncomingConnection(final Edge<? extends View<?>, Node> edge) {
            deleteConnector(edge);
        }

        @Override
        public void deleteOutgoingConnection(final Edge<? extends View<?>, Node> edge) {
            deleteConnector(edge);
        }

        @Override
        public void deleteEdge(final Edge<? extends View<?>, Node> candidate) {
            log("DeleteCanvasConnectorCommand [candidate=" + candidate.getUUID() + "]");
            getCommand().addCommand(new DeleteCanvasConnectorCommand(candidate));
        }

        @Override
        public void setEdgeTargetNode(final Node<?, Edge> targetNode,
                                      Edge<? extends View<?>, Node> candidate) {
            log("SetCanvasConnectionCommand [candidate=" + candidate.getUUID() + "]");
            getCommand().addCommand(new SetCanvasConnectionCommand(candidate));
        }

        @Override
        public void removeChild(final Element<?> parent,
                                final Node<?, Edge> candidate) {
            log("RemoveCanvasChildCommand [parent=" + parent.getUUID() + ", candidate=" + candidate.getUUID() + "]");
            getCommand().addCommand(new RemoveCanvasChildCommand((Node) parent,
                                                                 candidate));
        }

        @Override
        public void removeDock(final Node<?, Edge> parent,
                               final Node<?, Edge> candidate) {
            // No action required on the canvas side, as the shape for candidate is ensured to be deleted.
        }

        @Override
        public void deleteNode(final Node<?, Edge> node) {
            log("DeleteCanvasNodeCommand [node=" + node.getUUID() + "]");
            getCommand().addCommand(new DeleteCanvasNodeCommand(node));
        }

        private void deleteConnector(final Edge<? extends View<?>, Node> connector) {
            log("SetCanvasConnectionCommand [connector=" + connector.getUUID() + "]");
            getCommand().addCommand(new SetCanvasConnectionCommand(connector));
        }
    }

    private void log(final String message) {
        LOGGER.log(Level.FINE,
                   message);
    }
}
