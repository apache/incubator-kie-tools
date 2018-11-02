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
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.SafeDeleteNodeCommand;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public class DeleteNodeCommand extends AbstractCanvasGraphCommand {

    protected final Node candidate;
    protected final SafeDeleteNodeCommand.Options options;
    protected transient CanvasDeleteProcessor deleteProcessor;

    public DeleteNodeCommand(final Node candidate) {
        this(candidate,
             SafeDeleteNodeCommand.Options.defaults());
    }

    public DeleteNodeCommand(final Node candidate,
                             final SafeDeleteNodeCommand.Options options) {
        this(candidate, options, new CanvasDeleteProcessor(options));
    }

    @SuppressWarnings("unchecked")
    public DeleteNodeCommand(final Node candidate,
                             final SafeDeleteNodeCommand.Options options,
                             final CanvasDeleteProcessor deleteProcessor) {
        this.candidate = candidate;
        this.options = options;
        this.deleteProcessor = deleteProcessor;
    }

    public Node getCandidate() {
        return candidate;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
        return new SafeDeleteNodeCommand(candidate,
                                         deleteProcessor,
                                         options);
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler context) {
        return deleteProcessor.getCommand();
    }

    CompositeCommand<AbstractCanvasHandler, CanvasViolation> getCommand() {
        return deleteProcessor.getCommand();
    }

    public static class CanvasDeleteProcessor implements SafeDeleteNodeCommand.SafeDeleteNodeCommandCallback {

        private transient CompositeCommand<AbstractCanvasHandler, CanvasViolation> command;
        private final SafeDeleteNodeCommand.Options options;

        public CanvasDeleteProcessor(final SafeDeleteNodeCommand.Options options) {
            this.options = options;
            this.command = new CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation>()
                    .reverse()
                    .build();
        }

        @Override
        public void deleteCandidateConnector(final Edge<? extends View<?>, Node> connector) {
            doDeleteConnector(connector);
        }

        @Override
        public boolean deleteConnector(final Edge<? extends View<?>, Node> connector) {
            return doDeleteConnector(connector);
        }

        @Override
        public void setEdgeTargetNode(final Node<? extends View<?>, Edge> targetNode,
                                      Edge<? extends ViewConnector<?>, Node> candidate) {
            getCommand().addCommand(new SetCanvasConnectionCommand(candidate));
        }

        @Override
        public void removeChild(final Element<?> parent,
                                final Node<?, Edge> candidate) {
            getCommand().addCommand(new RemoveCanvasChildCommand((Node) parent,
                                                                 candidate));
        }

        @Override
        public void removeDock(final Node<?, Edge> parent,
                               final Node<?, Edge> candidate) {
            getCommand().addCommand(new CanvasUndockNodeCommand(parent, candidate));
        }

        @Override
        public void deleteCandidateNode(final Node<?, Edge> node) {
            doDeleteNode(node);
        }

        @Override
        public boolean deleteNode(final Node<?, Edge> node) {
            return doDeleteNode(node);
        }

        public CompositeCommand<AbstractCanvasHandler, CanvasViolation> getCommand() {
            return command;
        }

        public SafeDeleteNodeCommand.Options getOptions() {
            return options;
        }

        private boolean doDeleteNode(final Node<?, Edge> node) {
            if (!options.getExclusions().contains(node.getUUID())) {
                getCommand().addCommand(createDeleteCanvasNodeCommand(node));
                return true;
            }
            return false;
        }

        private boolean doDeleteConnector(final Edge<? extends View<?>, Node> connector) {
            if (!options.getExclusions().contains(connector.getUUID())) {
                getCommand().addCommand(new DeleteCanvasConnectorCommand(connector));
                return true;
            }
            return false;
        }

        protected DeleteCanvasNodeCommand createDeleteCanvasNodeCommand(final Node<?, Edge> node) {
            return new DeleteCanvasNodeCommand(node);
        }
    }
}
