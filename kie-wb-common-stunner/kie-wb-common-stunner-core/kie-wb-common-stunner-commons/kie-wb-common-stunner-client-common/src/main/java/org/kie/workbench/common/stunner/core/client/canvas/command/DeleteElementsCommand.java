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

import java.util.Collection;

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
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public class DeleteElementsCommand extends AbstractCanvasGraphCommand {

    private final Collection<Element> elements;
    private transient CompositeCommand<AbstractCanvasHandler, CanvasViolation> command;

    @SuppressWarnings("unchecked")
    public DeleteElementsCommand(Collection<Element> elements) {
        this.elements = elements;
        this.command = new CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation>()
                .reverse()
                .build();
    }

    public Collection<Element> getElements() {
        return elements;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
        return new org.kie.workbench.common.stunner.core.graph.command.impl.DeleteElementsCommand(() -> elements,
                                                                                                  new CanvasMultipleDeleteProcessor());
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler context) {
        return command;
    }

    protected CompositeCommand<AbstractCanvasHandler, CanvasViolation> getCommand() {
        return command;
    }

    protected class CanvasMultipleDeleteProcessor
            implements org.kie.workbench.common.stunner.core.graph.command.impl.DeleteElementsCommand.DeleteCallback {

        @Override
        public SafeDeleteNodeCommand.SafeDeleteNodeCommandCallback onDeleteNode(final Node<?, Edge> node,
                                                                                final SafeDeleteNodeCommand.Options options) {
            final DeleteNodeCommand.CanvasDeleteProcessor processor
                    = createProcessor(options);

            getCommand().addCommand(processor.getCommand());
            return processor;
        }

        @Override
        public void onDeleteEdge(final Edge<? extends View, Node> edge) {
            getCommand().addCommand(new DeleteCanvasConnectorCommand(edge));
        }

        protected DeleteNodeCommand.CanvasDeleteProcessor createProcessor(final SafeDeleteNodeCommand.Options options) {
            return new DeleteNodeCommand.CanvasDeleteProcessor(options) {
                @Override
                public boolean deleteConnector(final Edge<? extends View<?>, Node> connector) {
                    if (super.deleteConnector(connector)) {
                        options.getExclusions().add(connector.getUUID());
                        return true;
                    }
                    return false;
                }
            };
        }
    }
}
