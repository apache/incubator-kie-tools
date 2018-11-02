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

import java.util.Optional;
import java.util.function.Consumer;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public class CloneNodeCommand extends AbstractCanvasGraphCommand {

    private final Node candidate;
    private final String parentUuid;
    private Optional<Point2D> cloneLocation;
    private transient CompositeCommand<AbstractCanvasHandler, CanvasViolation> command;
    private final Optional<Consumer<Node>> cloneNodeCommandCallback;
    private final ManagedInstance<ChildrenTraverseProcessor> childrenTraverseProcessor;

    @SuppressWarnings("unchecked")
    public CloneNodeCommand(final Node candidate, final String parentUuid, final Point2D cloneLocation, final Consumer<Node> cloneNodeCommandCallback, final ManagedInstance<ChildrenTraverseProcessor> childrenTraverseProcessor) {
        this.candidate = candidate;
        this.cloneLocation = Optional.ofNullable(cloneLocation);
        this.parentUuid = parentUuid;
        this.cloneNodeCommandCallback = Optional.ofNullable(cloneNodeCommandCallback);
        this.childrenTraverseProcessor = childrenTraverseProcessor;
        this.command = buildCommand();
    }

    private CompositeCommand<AbstractCanvasHandler, CanvasViolation> buildCommand() {
        return new CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation>()
                .reverse()
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
        return new org.kie.workbench.common.stunner.core.graph.command.impl.CloneNodeCommand(candidate,
                                                                                             parentUuid,
                                                                                             getClonePosition(),
                                                                                             cloneNodeCallback(context),
                                                                                             childrenTraverseProcessor);
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler context) {
        return this.command;
    }

    protected Point2D getClonePosition() {
        return cloneLocation.orElseGet(() -> GraphUtils.getPosition((View) candidate.getContent()));
    }

    protected Consumer<Node> cloneNodeCallback(AbstractCanvasHandler context) {
        return clone -> {
            //check if not a redo operation, in case size == 1 it was set before
            if (!command.isEmpty()) {
                command = buildCommand();
            }
            command.addCommand(getCloneCanvasNodeCommand(GraphUtils.getParent(clone).asNode(), clone, context.getDiagram().getMetadata().getShapeSetId()));

            cloneNodeCommandCallback.ifPresent(callback -> callback.accept(clone));
        };
    }

    public CloneCanvasNodeCommand getCloneCanvasNodeCommand(Node parent, Node clone, String shapeId) {
        return new CloneCanvasNodeCommand(parent,
                                          clone,
                                          shapeId, childrenTraverseProcessor);
    }

    public ManagedInstance<ChildrenTraverseProcessor> getChildrenTraverseProcessor() {
        return childrenTraverseProcessor;
    }

    public Node getCandidate() {
        return candidate;
    }

    protected String getParentUuid() {
        return parentUuid;
    }
}